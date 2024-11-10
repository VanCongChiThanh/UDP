package BT3_Chat;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Server extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtPort;
	private JTextField txtSend;
	private JTextArea textArea;
	private DatagramSocket server;
	private static int port=1123;
	private InetAddress clientIP;
	private int clientPort;
	public static Set<String> listSK = new HashSet<>();
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server frame = new Server();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	private String receiveData(DatagramSocket server) throws IOException {
	    byte[] temp = new byte[1024];
	    DatagramPacket receivePacket = new DatagramPacket(temp, temp.length);
	    server.receive(receivePacket);
	    clientIP = receivePacket.getAddress();
	    clientPort = receivePacket.getPort();
	    checkDuplicate(receivePacket);

	    String message = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim(); 
	    for (String clientKey : listSK) {
	        if (!clientKey.equals(clientIP.toString() + ":" + clientPort)) {
	            sendData(message, server, clientKey);
	        }
	    }
	    
	    return message;
	}

	private void sendData(String value, DatagramSocket server, String clientKey) throws IOException {
	    String[] parts = clientKey.split(":");
	    InetAddress address = InetAddress.getByName(parts[0].substring(1));
	    int port = Integer.parseInt(parts[1]);
	    byte[] temp = value.getBytes();
	    DatagramPacket sendPacket = new DatagramPacket(temp, temp.length, address, port);
	    server.send(sendPacket);
	}



	private void checkDuplicate(DatagramPacket packet) {
	    String clientKey = packet.getAddress().toString() + ":" + packet.getPort();
	    listSK.add(clientKey);
	}
	

	/**
	 * Create the frame.
	 */
	public Server() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 432);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("PORT");
		lblNewLabel_1.setBounds(108, 60, 49, 14);
		contentPane.add(lblNewLabel_1);
		
		txtPort = new JTextField();
		txtPort.setText("6888");
		txtPort.setColumns(10);
		txtPort.setBounds(162, 57, 96, 20);
		contentPane.add(txtPort);
		txtPort.setText(String.valueOf(port));
		
		JLabel lblNewLabel = new JLabel("Server");
		lblNewLabel.setBounds(181, 32, 49, 14);
		contentPane.add(lblNewLabel);
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        try {
		            if (server != null && !server.isClosed()) {
		                server.close();
		                textArea.append("\nPrevious server stopped.\n");
		            }
		            port = Integer.parseInt(txtPort.getText());
		            server = new DatagramSocket(port);
		            textArea.append("Server started on port: " + port + "\n");
		            Thread receiveThread = new Thread(() -> {
		                while (true) {
		                    try {
		                        String message = receiveData(server);
		                        textArea.append(message + "\n");
		                    } catch (IOException e1) {
		                        e1.printStackTrace();
		                        break; 
		                    }
		                }
		            });
		            receiveThread.start();

		        } catch (IOException e1) {
		            e1.printStackTrace();
		        }
		    }
		});

		btnStart.setBounds(268, 56, 89, 23);
		contentPane.add(btnStart);
		
		textArea = new JTextArea();
		textArea.setBounds(28, 106, 379, 162);
		contentPane.add(textArea);
		
		txtSend = new JTextField();
		txtSend.setColumns(10);
		txtSend.setBounds(23, 305, 384, 20);
		contentPane.add(txtSend);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        try {
		        	String sms=txtSend.getText();
		        	textArea.append("Me:"+sms+"\n");
		            txtSend.setText("");
		            WriteServer write = new WriteServer(server, sms);
		            write.start();
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    }
		});

		btnSend.setBounds(160, 347, 89, 23);
		contentPane.add(btnSend);
	}
}
class WriteServer extends Thread {
	private DatagramSocket server;
	private String sms;

	public WriteServer(DatagramSocket server,String sms) {
		this.server = server;
		this.sms=sms;
	}

	@Override
	public void run() {
	    try {
	        for (String clientKey : Server.listSK) {
	            sendData("Server: " + sms, server, clientKey);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	private void sendData(String value, DatagramSocket server, String clientKey) throws IOException {
	    String[] parts = clientKey.split(":");
	    InetAddress address = InetAddress.getByName(parts[0].substring(1)); 
	    int port = Integer.parseInt(parts[1]);
	    byte[] temp = value.getBytes();
	    DatagramPacket sendPacket = new DatagramPacket(temp, temp.length, address, port);
	    server.send(sendPacket);
	}

}

