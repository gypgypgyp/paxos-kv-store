import java.io.Serializable;
import java.util.Random;

/**
 * Abstract base class for all Paxos roles (Proposer, Acceptor, Learner).
 * Implements common properties and methods, including node identity,
 * thread control, and simulated failure logic.
 */
public abstract class PaxosNode implements Runnable, Serializable {
  protected String nodeId;      // Unique identifier for this Paxos node
  protected boolean running = true;  // Indicates whether this node is active

  public PaxosNode(String nodeId) {
    this.nodeId = nodeId;
  }

  public void stop() {
    this.running = false;
  }

  public String getNodeId() {
    return nodeId;
  }

  public abstract void run();

  /**
   * Simulates random failure and recovery for the node by controlling the `running` flag.
   * Launches two internal threads:
   *  - One to periodically set the node to a failed state (running = false)
   *  - One to periodically recover the node (running = true)
   */
  protected void simulateFailureLoop() {
    new Thread(() -> {
      Random rand = new Random();
      while (true) {
        try {
          Thread.sleep(rand.nextInt(10000) + 10000);
          if (running) {
            System.out.println("[" + nodeId + "] Simulating failure...");
            running = false;
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();

    new Thread(() -> {
      Random rand = new Random();
      while (true) {
        try {
          Thread.sleep(rand.nextInt(2000) + 2000);
          if (!running) {
            System.out.println("[" + nodeId + "] Restarting...");
            running = true;
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
}