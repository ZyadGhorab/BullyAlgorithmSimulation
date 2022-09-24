package main;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

@Getter
@Setter
public class MainGUI {
  private JComboBox comboBox1;
  private JButton changeStatusButton;
  private JTextArea logs;
  private JPanel rootPanel;
  private JButton startElectionButton;
}
