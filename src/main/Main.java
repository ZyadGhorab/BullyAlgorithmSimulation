package main;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

  static MainGUI mainGUI;
  static JFrame frame;
  static List<Process> processes;

  static int numberOfProcesses = 6;

  public static void main(String[] args) throws IOException, InterruptedException {
    processes = new ArrayList<>();
    List<String[]> processArguments =
        new ArrayList<>() {
          {
            add(
                new String[] {
                  "java", "-jar", "Bully.jar", "6", "5006", "Normal", "3000", "config.txt"
                });
            add(
                new String[] {
                  "java", "-jar", "Bully.jar", "5", "5005", "Normal", "3000", "config.txt"
                });
            add(
                new String[] {
                  "java",
                  "-jar",
                  "Bully.jar",
                  "4",
                  "5004",
                  "Normal",
                  "3000",
                  "config.txt",
                  "Initiator"
                });
            add(
                new String[] {
                  "java", "-jar", "Bully.jar", "3", "5003", "Normal", "3000", "config.txt"
                });
            add(
                new String[] {
                  "java", "-jar", "Bully.jar", "2", "5002", "Normal", "3000", "config.txt"
                });
            add(
                new String[] {
                  "java", "-jar", "Bully.jar", "1", "5001", "Normal", "3000", "config.txt"
                });
          }
        };

    for (int i = 0; i < numberOfProcesses; i++) {
      ProcessBuilder processBuilder = new ProcessBuilder(processArguments.get(i));
      processBuilder.inheritIO();
      Process process = processBuilder.start();
      processes.add(process);
    }

    InitializeGUI();
  }

  private static void InitializeGUI() {
    /** Setting the server GUI window properties */
    mainGUI = new MainGUI();
    frame = new JFrame("Bully Algorithm");
    frame.setContentPane(mainGUI.getRootPanel());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(800, 600);
    frame.setLocationRelativeTo(null);
    frame.setResizable(false);
    mainGUI.getComboBox1().setModel(new DefaultComboBoxModel<>(processes.toArray()));
    frame.setVisible(true);

    ((DefaultCaret) mainGUI.getLogs().getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

    frame.addWindowListener(
        new java.awt.event.WindowAdapter() {
          @Override
          public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            for (Process process : processes) {
              process.destroy();
            }
          }
        });
  }
}
