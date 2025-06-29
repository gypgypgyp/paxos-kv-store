import java.util.List;

/**
 * RunnableProposer is a wrapper class that adapts the Proposer into a PaxosNode,
 * allowing it to be run as a thread and participate in failure simulation.
 *
 * This class delegates Paxos proposal logic to an internal Proposer instance,
 * and uses the `running` flag to simulate failure and recovery behavior.
 */
public class RunnableProposer extends PaxosNode {
  private Proposer internalProposer;

  public RunnableProposer(String nodeId, List<Acceptor> acceptors, List<Learner> learners) {
    super(nodeId);
    this.internalProposer = new Proposer(acceptors, learners);
  }

  public boolean put(String key, String value) {
    return running && internalProposer.put(key, value);
  }

  public boolean delete(String key) {
    return running && internalProposer.delete(key);
  }

  @Override
  public void run() {
    System.out.println("[Proposer " + nodeId + "] Running with failure simulation");
    simulateFailureLoop(); // simulate failure
  }
}
