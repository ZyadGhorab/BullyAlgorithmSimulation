package bully;

import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

@Setter
public class Node {

  private final int uuid;
  private final int port;
  private final NodeType type;
  private final int timeout;
  private Socket socket;
  private PrintWriter socketWriter;
  private BufferedReader socketReader;

  private boolean isCoordinator;
  private boolean isInitiator;
  private HashMap<Integer, Node> nodes;

  public Node(int uuid, int port, NodeType type, int timeout) {
    this.port = port;
    this.uuid = uuid;
    this.type = type;
    this.timeout = timeout;
    setInitiator(false);
    setCoordinator(false);
  }

  public int getUuid() {
    return uuid;
  }

  public int getPort() {
    return port;
  }

  public NodeType getType() {
    return type;
  }

  public boolean elect() {
    connect();

    boolean ok = false;

    Bully.logger.log(String.format("Send Elect from %d to %d.", Bully.self.getUuid(), getUuid()));

    socketWriter.println(Bully.self.getUuid());
    socketWriter.println(Message.ELECT);

    if (getMessage() == Message.OK) {
      ok = true;
      Bully.logger.logInternal(String.format("Received OKAY from %d.", getUuid()));
    }

    disconnect();

    return ok;
  }

  public void sendCoordinatorMessage() {
    connect();

    Bully.logger.log(
        String.format("Send Coordinator Message From %d to %d.", Bully.self.getUuid(), getUuid()));

    socketWriter.println(Bully.self.getUuid());
    socketWriter.println(Message.COORDINATOR);

    disconnect();
  }

  private Message getMessage() {
    String line;
    try {
      while ((line = socketReader.readLine()) != null) {
        return Message.valueOf(line);
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }

    return null;
  }

  private void connect() {
    try {
      socket = new Socket("localhost", this.getPort());
      socket.setSoTimeout(timeout);
      socketWriter = new PrintWriter(socket.getOutputStream(), true);
      socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  private void disconnect() {
    try {
      socket.close();
      socket = null;
      socketWriter = null;
      socketReader = null;
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }
}
