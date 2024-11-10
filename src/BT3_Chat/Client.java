package BT3_Chat;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextArea textArea;
    private JTextField textField;
    private JTextField portField;
    private JButton sendButton;
    private JButton joinButton;
    private DatagramSocket client;
    private InetAddress host;
    private int port=1123;
    private String name;

    public Client(InetAddress host) {
        this.host = host;

        setTitle("Chat Client");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel portLabel = new JLabel("Port:");
        portLabel.setBounds(10, 10, 40, 25);
        add(portLabel);

        portField = new JTextField();
        portField.setBounds(50, 10, 100, 25);
        add(portField);
        portField.setText(String.valueOf(port));

        joinButton = new JButton("Join");
        joinButton.setBounds(160, 10, 80, 25);
        add(joinButton);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBounds(10, 50, 360, 250);
        add(textArea);

        textField = new JTextField();
        textField.setBounds(10, 310, 260, 30);
        add(textField);

        sendButton = new JButton("Send");
        sendButton.setBounds(280, 310, 90, 30);
        sendButton.setEnabled(false); 
        add(sendButton);

        setVisible(true);

        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    port = Integer.parseInt(portField.getText());
                    client = new DatagramSocket();
                    name = JOptionPane.showInputDialog(Client.this, "Nhập vào tên của bạn:");
                    if (name != null && !name.isEmpty()) {
                        client.send(createPacket(name + " đã tham gia vào phòng chat!"));
                        textArea.append("Đã tham gia phòng chat trên cổng: " + port + "\n");
                        sendButton.setEnabled(true);
                        new ReadClient(client, textArea).start(); 
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Client.this, "Lỗi: " + ex.getMessage());
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String sms = textField.getText();
                    if (!sms.isEmpty()) {
                        DatagramPacket packet = createPacket(name + ": " + sms);
                        client.send(packet);
                        textField.setText(""); 
                    }
                    textArea.append("Me:" +sms+"\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private DatagramPacket createPacket(String value) {
        byte[] arrData = value.getBytes();
        return new DatagramPacket(arrData, arrData.length, host, port);
    }

    public static void main(String[] args) throws IOException {
        new Client(InetAddress.getLocalHost());
    }
}

class ReadClient extends Thread {
    private DatagramSocket client;
    private JTextArea textArea;

    public ReadClient(DatagramSocket client, JTextArea textArea) {
        this.client = client;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String sms = receiveData(client);
                textArea.append(sms + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String receiveData(DatagramSocket client) throws IOException {
        byte[] temp = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(temp, temp.length);
        client.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
    }
}
