package bully;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class Bully {

  public static Node self;
  public static Logger logger;
  int timeout;
  boolean isInitiator = false;
  boolean isCoordinator = false;
  BufferedReader socketReader;
  PrintWriter socketWriter;
  HashMap<Integer, Node> nodes;

  public static void main(String[] args) {
    System.out.println("Args: " + Arrays.toString(args));

    Bully bully = new Bully();
    bully.getArgs(args);
    logger = new Logger(Bully.self.getUuid());

    bully.listen();
  }

  void getArgs(String[] args) {
    try {
      Bully.self =
          new Node(
              Integer.parseInt(args[0]),
              Integer.parseInt(args[1]),
              NodeType.valueOf(args[2]),
              Integer.parseInt(args[3]));
      timeout = Integer.parseInt(args[3]);
      nodes = getNodesConfiguration(args[4]);
      Bully.self.setNodes(nodes);
      if (args.length > 5) {
        isInitiator = args[5].equalsIgnoreCase("Initiator");
        isCoordinator = args[5].equalsIgnoreCase("Coordinator");
        if (isInitiator) {
          Bully.self.setInitiator(true);
        }
        if (isCoordinator) {
          Bully.self.setCoordinator(true);
        }
      }

    } catch (Exception e) {
      System.err.println(e); // TODO: DEBUG

      System.err.println(new IncorrectArgumentsException().getMessage());
      System.exit(-1);
    }
  }

  HashMap<Integer, Node> getNodesConfiguration(String file) throws Exception {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    HashMap<Integer, Node> nodes = new HashMap<>();

    String line;
    String[] data;

    Node newNode;
    while ((line = reader.readLine()) != null) {
      data = line.split(",");
      newNode =
          new Node(
              Integer.parseInt(data[0]), Integer.parseInt(data[1]), NodeType.Normal, this.timeout);
      nodes.put(newNode.getUuid(), newNode);
      System.out.println(
          String.format("Loaded node: %d, port: %d", newNode.getUuid(), newNode.getPort()));
    }

    reader.close();
    return nodes;
  }

  void listen() {
    boolean listening = true;

    if (this.isInitiator) {
      startElection();
    }

    Socket clientSocket;
    try (ServerSocket serverSocket = new ServerSocket(self.getPort())) {
      while (listening) {
        clientSocket = serverSocket.accept();
        clientSocket.setSoTimeout(timeout);
        receive(clientSocket);
      }
    } catch (IOException e) {
      System.err.println("Could not listen on port " + self.getPort());
      System.exit(-1);
    }
  }

  private void receive(Socket clientSocket) {
    try {
      socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);

      int senderID = Integer.parseInt(socketReader.readLine());
      Message message = Message.valueOf(socketReader.readLine());

      if (message == Message.ELECT) {

        socketWriter.println(Message.OK);
        Bully.logger.log(String.format("Send OKAY to %d.", senderID));

        startElection();
      } else if (message == Message.COORDINATOR) {
        Bully.logger.log(String.format("Received Coordinator message from %d.", senderID));
      }

    } catch (Exception e) {
      System.err.println(e.getMessage());
    }

    try {
      clientSocket.close();
      socketReader = null;
      socketWriter = null;

    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  void startElection() {
    logger.logInternal(String.format("Triggering an election from process %d.", self.getUuid()));

    boolean ok = false;
    Collection<Node> all = nodes.values();

    for (Node n : all) {
      if (n.getUuid() > self.getUuid()) {
        ok = n.elect() || ok;
      }
    }

    // No OK responses, become the new leader.
    if (ok == false) {
      logger.log("Timeout Triggered.");
      sendCoordinator();
    }
  }

  void sendCoordinator() {
    logger.logInternal("No response send out Coordinator message.");

    Collection<Node> all = nodes.values();

    for (Node n : all) {
      // Send Coordinator to all except self
      if (n.getUuid() != self.getUuid()) {
        n.sendCoordinatorMessage();
      }
    }
  }
}
