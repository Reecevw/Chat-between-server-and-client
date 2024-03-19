import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class ServerProcessor extends JFrame {
    private final int totalClients = 100;
    private final int PORT = 6789;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket connection;
    private ServerSocket server;
    private JTextArea chatArea;
    private JButton sendButton;
    private JLabel mainLayer;
    private JLabel subLayer;
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private JTextField mainTextArea;
    private JLabel status;
    public ServerProcessor() {
        initComponents();
        this.setTitle("Server");
        this.setVisible(true);
        status.setVisible(true);
    }
    synchronized public void execute() {
        try {
            server = new ServerSocket(PORT, totalClients);
            while (true) {
                try {
                    status.setText("Try to Connect...");
                    connection = server.accept();
                    status.setText("Now Connected to " +
                            connection.getInetAddress().getHostName());
                    outputStream = new ObjectOutputStream(connection.getOutputStream());
                    outputStream.flush();
                    inputStream = new ObjectInputStream(connection.getInputStream());
                    whileChatting();
                } catch (EOFException eofException) {
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    private void whileChatting() throws IOException {
        String message = "";
        mainTextArea.setEditable(true);
        do {
            try {
                message = (String) inputStream.readObject();
                chatArea.append("\n" + message);
            } catch (ClassNotFoundException classNotFoundException) {
            }
        } while (!message.equals("Client::END"));
    }
    private void initComponents() {
        mainPanel = new JPanel();
        scrollPane = new JScrollPane();
        chatArea = new JTextArea();
        mainTextArea = new JTextField();
        sendButton = new JButton();
        status = new JLabel();
        subLayer = new JLabel();
        mainLayer = new JLabel();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        mainPanel.setLayout(null);
        chatArea.setColumns(20);
        chatArea.setRows(5);
        scrollPane.setViewportView(chatArea);
        mainPanel.add(scrollPane);
        scrollPane.setBounds(10, 90, 480, 250);
        mainTextArea.addActionListener(this::textFieldActionPerformed);
        mainPanel.add(mainTextArea);
        mainTextArea.setBounds(10, 350, 400, 40);
        sendButton.setText("Send");
        sendButton.addActionListener(this::mainActionButtonPerformed);
        mainPanel.add(sendButton);
        sendButton.setBounds(410, 350, 80, 40);
        status.setText("...");
        mainPanel.add(status);
        status.setBounds(10, 60, 300, 40);
        subLayer.setText("Server");
        subLayer.setToolTipText("");
        mainPanel.add(subLayer);
        subLayer.setBounds(80, 10, 190, 60);
        mainPanel.add(mainLayer);
        mainLayer.setBounds(0, 35, 460, 410);
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, GroupLayout.Alignment.TRAILING,
                                GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(mainPanel, GroupLayout.PREFERRED_SIZE, 476,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        setSize(new java.awt.Dimension(500, 427));
        setLocationRelativeTo(null);
    }
    private void mainActionButtonPerformed(ActionEvent evt) {
        sendMessage(mainTextArea.getText());
        mainTextArea.setText("");
    }
    private void textFieldActionPerformed(ActionEvent evt) {
        sendMessage(mainTextArea.getText());
        mainTextArea.setText("");
    }
    private void sendMessage(String message) {
        try {
            chatArea.append("\nServer: "+message);
            outputStream.writeObject(" Server: " + message);
            outputStream.flush();
        } catch (IOException ioException) {
            chatArea.append("\n Unable to Send Message");
        }
    }
}


