import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Naming;
import java.util.*;

public class PaxosLauncher {
  public static void main(String[] args) throws Exception {
    List<Acceptor> allAcceptors = new ArrayList<>();
    List<PaxosServer> servers = new ArrayList<>();
    List<Learner> allLearners = new ArrayList<>();

    Registry registry = LocateRegistry.createRegistry(1099);

    for (int i = 0; i < 5; i++) {
      PaxosServer server = new PaxosServer("Server" + i, allAcceptors, allLearners);
      allAcceptors.add(server.acceptor);
      allLearners.add(server.getLearner());
      servers.add(server);
      Naming.rebind("rmi://localhost/Server" + i, server);
      new Thread(server).start();
    }


    System.out.println("All servers registered. RMI Registry ready.");
  }

}
