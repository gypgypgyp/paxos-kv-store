import java.util.concurrent.ConcurrentHashMap;

/**
 * Learner is a Paxos role responsible for applying the agreed-upon values
 * to the local key-value store once consensus is achieved.
 *
 * It acts as the final destination in the Paxos pipeline, maintaining the
 * state of the distributed key-value store.
 */
public class Learner extends PaxosNode {
  private ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

  public Learner(String nodeId) {
    super(nodeId);
  }

  public void learn(String key, String value) {
    store.put(key, value);
    System.out.println("[Learner " + nodeId + "] Learned: " + key + " = " + value);
  }

  public void delete(String key) {
    store.remove(key);
    System.out.println("[Learner " + nodeId + "] Deleted: " + key);
  }

  public String get(String key) {
    return store.getOrDefault(key, null);
  }

  @Override
  public void run() {
    System.out.println("[Learner] Running " + nodeId);
    simulateFailureLoop(); // simulate failure
  }

}
