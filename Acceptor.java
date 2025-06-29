import java.util.Random;

/**
 * Acceptor is a core Paxos role responsible for participating in consensus.
 * It handles prepare and accept requests from Proposers and ensures
 * consistency by maintaining the highest promised proposal ID.
 */
public class Acceptor extends PaxosNode {
  private Learner learner;           // Reference to associated learner to apply values after acceptance
  private int promisedId = -1;       // Highest proposal ID this acceptor has promised not to accept lower ones

  public Acceptor(String nodeId, Learner learner) {
    super(nodeId);
    this.learner = learner;
  }


  /**
   * Handles the Paxos "prepare" phase.
   * If the incoming proposalId is greater than any previously promised,
   * this acceptor promises not to accept proposals with lower IDs.
   *
   * @param proposalId The ID of the incoming proposal
   * @param key        The key involved (used for logging)
   * @return true if promise is made, false otherwise
   */
  public synchronized boolean promise(int proposalId, String key) {
    if (!running) return false;
    if (proposalId > promisedId) {
      promisedId = proposalId;
      System.out.println("[Acceptor " + nodeId + "] Promised for proposal " + proposalId + " on key " + key);
      return true;
    } else {
      return false;
    }
  }


  public synchronized boolean accept(Proposal proposal) {
    if (!running) return false;
    if (proposal.proposalId >= promisedId) {
      System.out.println("[Acceptor " + nodeId + "] Accepted proposal " + proposal.proposalId + " for " + proposal.operation + "(" + proposal.key + ")");
      if ("PUT".equals(proposal.operation)) {
        learner.learn(proposal.key, proposal.value);
      } else if ("DEL".equals(proposal.operation)) {
        learner.delete(proposal.key);
      }
      return true;
    }
    return false;
  }

  @Override
  public void run() {
    System.out.println("[Acceptor] Running " + nodeId);
    simulateFailureLoop();
  }

}
