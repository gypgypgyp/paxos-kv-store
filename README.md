# paxos-kv-store

Paxos Fault-Tolerant Key-Value Store

1. Overview
This project implements a fault-tolerant Key-Value Store using the Paxos consensus algorithm.
The system supports five replicated servers communicating via Java RMI, where each server hosts a Proposer, Acceptor, and Learner component. A
ll roles run as threads and simulate random failure and recovery.
The system processes distributed PUT, GET, and DELETE operations while ensuring consistency through quorum-based consensus.

2. Architecture
- PaxosClient.java: A CLI-based client that connects to one of five servers via RMI, issues commands, and prepopulates the store.
- PaxosServer.java: RMI-exposed server that wraps Proposer, Acceptor, and Learner roles, each running on separate threads.
- RunnableProposer.java: Threadable wrapper for the Proposer, enabling simulated failure handling.
- Proposer.java: Implements Paxos proposal and retry logic to achieve agreement among acceptors and notify learners.
- Acceptor.java: Responds to prepare and accept requests, using promise rules and forwarding accepted values to learners.
- Learner.java: Finalizes committed values in a thread-safe key-value store.
- PaxosNode.java: Abstract base class for Paxos roles, with built-in failure simulation logic.
- KVStore.java: Java RMI interface defining PUT, GET, DELETE operations.
- PaxosLauncher.java: Starts the RMI registry and launches five PaxosServer instances.

3. How to Run
Compile the program:
```
javac *.java
```

Start the Paxos servers and bind them to RMI:
```
java PaxosLauncher
```

In a separate terminal, start the client:
```
java PaxosClient
```

Use the client to issue interactive commands:
```
PUT key value
GET key
DEL key
EXIT
```

4. Features
- Full implementation of Paxos consensus protocol using multithreaded roles
- Simulated random failure and recovery of proposers, acceptors, and learners
- Client-driven operations with automatic retry on consensus failure
- Quorum-based acceptance ensures correctness even with partial node failures
- Modular code design with reusable base class for Paxos roles

5. Notes
- GET operations are local to the learner and do not require consensus
- PUT and DELETE go through the full Paxos protocol
- At least 5 of each operation (PUT, GET, DEL) are performed automatically on startup
- Acceptors fail and recover at randomized intervals to simulate a fault-prone system

6. Known Limitations
- Paxos roles are restarted in-memory; persistent recovery is not implemented
- Leader election is not used; proposers operate independently
- Simulated failures may interfere with quorum in rare high-overlap scenarios
