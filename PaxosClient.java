import java.rmi.Naming;
import java.util.Random;
import java.util.Scanner;


/**
 * PaxosClient connects to the distributed Paxos-based KV store via RMI.
 * It prepopulates the system with initial PUT/GET/DEL operations and
 * provides an interactive CLI for user input.
 */
public class PaxosClient {
  public static void main(String[] args) throws InterruptedException {
      try {
        System.out.println("[Client] Waiting for servers to initialize...");
        Thread.sleep(10000); // Delay to allow acceptors to restart at least once
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      Scanner scanner = new Scanner(System.in);
      Random rand = new Random();

      // Prepopulate with 5 PUTs
      String[] keys = {"TK1", "TK2", "TK3", "TK4", "TK5"};
      String[] values = {"TV1", "TV2", "TV3", "TV4", "TV5"};

      for (int i = 0; i < 5; i++) {
        try {
          KVStore stub = (KVStore) Naming.lookup("rmi://localhost/Server" + rand.nextInt(5));
          stub.put(keys[i], values[i]);
          System.out.println("[Client] Auto-PUT: " + keys[i] + " = " + values[i]);
        } catch (Exception e) {
          System.out.println("[Client] Failed to PUT " + keys[i]);
        }
      }

  // 5 GETs
      for (int i = 0; i < 5; i++) {
        try {
          KVStore stub = (KVStore) Naming.lookup("rmi://localhost/Server" + rand.nextInt(5));
          String value = stub.get(keys[i]);
          System.out.println("[Client] Auto-GET: " + keys[i] + " = " + value);
        } catch (Exception e) {
          System.out.println("[Client] Failed to GET " + keys[i]);
        }
      }

  // 5 DELs
      for (int i = 0; i < 5; i++) {
        try {
          KVStore stub = (KVStore) Naming.lookup("rmi://localhost/Server" + rand.nextInt(5));
          stub.delete(keys[i]);
          System.out.println("[Client] Auto-DEL: " + keys[i]);
        } catch (Exception e) {
          System.out.println("[Client] Failed to DEL " + keys[i]);
        }
      }

      while (true) {
        System.out.print("Enter command (PUT key value | GET key | DEL key | EXIT): ");
        String line = scanner.nextLine();
        if (line.equalsIgnoreCase("EXIT")) break;

        String[] parts = line.trim().split(" ");
        int serverIndex = rand.nextInt(5);

        try {
          KVStore stub = (KVStore) Naming.lookup("rmi://localhost/Server" + serverIndex);

          if (parts[0].equalsIgnoreCase("PUT") && parts.length == 3) {
            stub.put(parts[1], parts[2]);
          } else if (parts[0].equalsIgnoreCase("GET") && parts.length == 2) {
            String value = stub.get(parts[1]);
            System.out.println("[Client] GET Result: " + value);
          } else if (parts[0].equalsIgnoreCase("DEL") && parts.length == 2) {
            stub.delete(parts[1]);
          } else {
            System.out.println("Invalid command.");
          }
          System.out.println("Operation succeed!");

        } catch (Exception e) {
//          System.err.println("[Client] Failed to connect to Server" + serverIndex);
//          e.printStackTrace();
          if (parts[0].equalsIgnoreCase("PUT") && parts.length == 3) {
            System.out.println("[Client] Failed to PUT " + parts[1] + ". Please try again.");
          } else if (parts[0].equalsIgnoreCase("DEL") && parts.length == 2) {
            System.out.println("[Client] Failed to DEL " + parts[1] + ". Please try again.");
          } else {
            System.out.println("[Client] Operation failed." + " Please try again.");
          }
        }
      }

      scanner.close();
  }
}
