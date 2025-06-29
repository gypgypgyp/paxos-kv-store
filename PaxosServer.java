import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


/**
 * PaxosServer acts as a remote-accessible KVStore node, exposing GET/PUT/DELETE
 * operations via Java RMI. Internally, it wraps the core Paxos components:
 * Proposer, Acceptor, and Learner.
 *
 * Each server instance is uniquely identified by a serverId and runs its Paxos
 * roles as separate threads with simulated failure/restart capabilities.
 */
public class PaxosServer extends UnicastRemoteObject implements KVStore, Runnable {
  private String serverId;
//  private Proposer proposer;
  RunnableProposer proposer;
  Acceptor acceptor;
  Learner learner;
  public static List<Learner> allLearners;


  /**
   * Constructs a new PaxosServer with its own learner, acceptor, and proposer.
   *
   * @param serverId     Unique server identifier
   * @param allAcceptors List of all acceptors in the system
   * @param allLearners  List of all learners in the system (shared for quorum operations)
   * @throws RemoteException Required by RMI interface
   */
  public PaxosServer(String serverId, List<Acceptor> allAcceptors, List<Learner> allLearners) throws RemoteException {
    this.serverId = serverId;
    this.learner = new Learner("Learner-" + serverId);
    this.acceptor = new Acceptor("Acceptor-" + serverId, learner);
    PaxosServer.allLearners = allLearners;
    this.proposer = new RunnableProposer("Proposer-" + serverId, allAcceptors, allLearners);

  }


  public Learner getLearner() {
    return learner;
  }


  @Override
  public String get(String key) throws RemoteException {
    System.out.println("[Server " + serverId + "] Received GET(" + key + ")");
    return learner.get(key);
  }


  @Override
  public void put(String key, String value) throws RemoteException {
    System.out.println("[Server " + serverId + "] Received PUT(" + key + ", " + value + ")");
    boolean success = proposer.put(key, value);
    if (!success) {
      throw new RemoteException("[Server " + serverId + "] Consensus failed for PUT(" + key + ")");
    }
  }

  @Override
  public void delete(String key) throws RemoteException {
    System.out.println("[Server " + serverId + "] Received DEL(" + key + ")");
    boolean success = proposer.delete(key);
    if (!success) {
      throw new RemoteException("[Server " + serverId + "] Consensus failed for DEL(" + key + ")");
    }
  }


  @Override
  public void run() {
    new Thread(acceptor).start();
    new Thread(learner).start();
    new Thread(proposer).start();
  }
}
