import java.util.List;

/**
 * Proposer is responsible for initiating Paxos rounds by generating proposals
 * and attempting to reach consensus with a majority of Acceptors.
 */
public class Proposer {
  private static int proposalCounter = 0;

  private List<Acceptor> acceptors;
  private List<Learner> learners;

  public Proposer(List<Acceptor> acceptors, List<Learner> learners) {
    this.acceptors = acceptors;
    this.learners = learners;
  }

  /**
   * Core method to propose a value for a given key and operation (PUT or DELETE).
   * Retries up to 3 times if consensus is not achieved.
   *
   * @param key       The key involved in the operation
   * @param value     The value to store (null for DELETE)
   * @param operation The operation type: "PUT" or "DEL"
   * @return true if consensus was reached and learners were updated; false otherwise
   */
  public boolean propose(String key, String value, String operation) {
    int retries = 3;
    while (retries-- > 0) {
      int proposalId = ++proposalCounter;
      Proposal proposal = new Proposal(proposalId, key, value, operation);
      int promises = 0;

      for (Acceptor acc : acceptors) {
        if (acc.promise(proposalId, key)) {
          promises++;
        }
      }

      if (promises > acceptors.size() / 2) {
        int accepts = 0;
        for (Acceptor acc : acceptors) {
          if (acc.accept(proposal)) {
            accepts++;
          }
        }

        if (accepts > acceptors.size() / 2) {
          for (Learner l : learners) {
            if ("PUT".equals(operation))
              l.learn(key, value);
            else if ("DEL".equals(operation))
              l.delete(key);
          }
          return true; // return true if update succeed
        }
      } else {
        System.out.println("[Proposer] Proposal " + proposalId + " for key " + key
            + " rejected in prepare phase. Retrying...");
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
    return false; // return false if 3 times failure
  }

  public boolean put(String key, String value) {
    return propose(key, value, "PUT");
  }

  public boolean delete(String key) {
    return propose(key, null, "DEL");
  }


}