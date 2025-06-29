import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * KVStore defines a remote interface for a distributed key-value store.
 * This interface is used by clients to perform operations over RMI.
 *
 * All methods throw RemoteException to handle communication failures
 * in the remote method invocation process.
 */
public interface KVStore extends Remote {
  void put(String key, String value) throws RemoteException;
  String get(String key) throws RemoteException;
  void delete(String key) throws RemoteException;
}