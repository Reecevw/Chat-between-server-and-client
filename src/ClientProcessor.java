import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

class ClientProcessor extends JFrame {
    private final String serverIP;
    private final int SERVER_PORT = 6789;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String initialMessage = "";
    private Socket socketConnection;
    private JTextArea chatArea;
    private JButton sendButton;
    private JLabel mainLayer;
    private JLabel subLayer;
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private JTextField mainTextArea;
    private JLabel status;
    public ClientProcessor(String serverIp) {
        initComponents();
        this.setTitle("Client");
        this.setVisible(true);
        this.status.setVisible(true);
        this.serverIP = serverIp;
    }
    private void initComponents() {
        mainPanel = new JPanel();
        mainTextArea = new JTextField();
        sendButton = new JButton();
        scrollPane = new JScrollPane();
        chatArea = new JTextArea();
        subLayer = new JLabel();
        status = new JLabel();
        mainLayer = new JLabel();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        mainPanel.setLayout(null);
        mainTextArea.setToolTipText("text\tType your message here...");
        mainTextArea.addActionListener(this::mainTextFieldActionPerformed);
        mainPanel.add(mainTextArea);
        mainTextArea.setBounds(10, 370, 410, 40);
        sendButton.setText("Send");
        sendButton.addActionListener(this::mainActionButtonPerformed);
        mainPanel.add(sendButton);
        sendButton.setBounds(420, 370, 80, 40);
        chatArea.setColumns(20);
        chatArea.setRows(5);
        scrollPane.setViewportView(chatArea);
        mainPanel.add(scrollPane);
        scrollPane.setBounds(10, 80, 490, 280);
        subLayer.setText("Client");
        mainPanel.add(subLayer);
        subLayer.setBounds(140, 20, 180, 40);
        status.setText("...");
        mainPanel.add(status);
        status.setBounds(10, 50, 300, 40);
        mainPanel.add(mainLayer);
        mainLayer.setBounds(0, 0, 400, 400);
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, GroupLayout.PREFERRED_SIZE, 508,
                                GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, GroupLayout.PREFERRED_SIZE, 419,
                                GroupLayout.PREFERRED_SIZE)
        );
        setSize(new java.awt.Dimension(508, 441));
        setLocationRelativeTo(null);
    }
    private void mainTextFieldActionPerformed(ActionEvent evt) {
        sendMessage(mainTextArea.getText());
        mainTextArea.setText("");
    }
    private void mainActionButtonPerformed(ActionEvent evt) {
        sendMessage(mainTextArea.getText());
        mainTextArea.setText("");
    }
    public void execute() {
        try {
            status.setText("Connecting...");
            try {
                socketConnection = new Socket(InetAddress.getByName(serverIP), SERVER_PORT);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(null, "Server Might Be Down!", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
            status.setText("Connected to: " +
                    socketConnection.getInetAddress().getHostName());
            objectOutputStream = new
                    ObjectOutputStream(socketConnection.getOutputStream());
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(socketConnection.getInputStream());
            whileChatting();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    private void whileChatting() throws IOException {
        mainTextArea.setEditable(true);
        do {
            try {
                initialMessage = (String) objectInputStream.readObject();
                chatArea.append("\n" + initialMessage);
            } catch (ClassNotFoundException classNotFoundException) {
            }
        } while (!initialMessage.equals("Client::END"));
    }
    private void sendMessage(String message) {
        try {
            chatArea.append("\nClient: "+message);
            objectOutputStream.writeObject(" Client: " +
                    message);
            objectOutputStream.flush();
        } catch (IOException ioException) {
            chatArea.append("\nUnable to Send Message");
        }
    }
}