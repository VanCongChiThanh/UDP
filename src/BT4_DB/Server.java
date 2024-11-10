package BT4_DB;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.mysql.jdbc.Statement;



public class Server extends JFrame {

	private JPanel contentPane;
    private JTextField textField;
    private JTextArea textArea;
    private DatagramSocket serverSocket;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/login?useSSL=false";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "12345Thanh";
	/**
	 * Launch the application.
	 */
	/**
	 * Create the frame.
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
	public Server() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 473, 470);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("PORT");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel.setBounds(10, 10, 50, 38);
        contentPane.add(lblNewLabel);

        textField = new JTextField();
        textField.setBounds(99, 10, 226, 38);
        contentPane.add(textField);
        textField.setColumns(10);

        JButton btnNewButton = new JButton("START");
        btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnNewButton.setBounds(335, 10, 96, 38);
        contentPane.add(btnNewButton);
        
        btnNewButton.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
                int port = Integer.parseInt(textField.getText());


                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                    textArea.append("\n Đóng socket cũ trước khi khởi động lại.");
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            serverSocket = new DatagramSocket(port);
                            textArea.append("\n Server is started");
                            while (true) {
                                byte[] receiveData = new byte[65507];
                                byte[] sendData = new byte[65507];
                                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                                serverSocket.receive(receivePacket);
                                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
                                textArea.append("\nServer nhận truy vấn: " + request);
                                InetAddress clientIPAddress = receivePacket.getAddress();
                                int clientPort = receivePacket.getPort();

                                String message = null;
								try {
									message = String.valueOf( handleClient(request))+"\n";
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

                                sendData = message.getBytes();
                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIPAddress, clientPort);
                                serverSocket.send(sendPacket);
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        JLabel lblNewLabel_1 = new JLabel("SERVER LOG");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel_1.setBounds(10, 79, 143, 38);
        contentPane.add(lblNewLabel_1);

        textArea = new JTextArea();
        textArea.setBounds(10, 127, 434, 277);
        contentPane.add(textArea);
	}
    private String handleClient(String request) {
              String response=null;

                if (request.toUpperCase().startsWith("SELECT")) {
                    response = executeSelect(request);
                } else if (request.toUpperCase().startsWith("INSERT") ||
                           request.toUpperCase().startsWith("UPDATE") ||
                           request.toUpperCase().startsWith("DELETE")) {
                    response = executeUpdate(request);
                } else {
                    response = "Invalid request";
                }
               return response;

    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    private static String executeSelect(String query) {
        StringBuilder result = new StringBuilder();

        try (Connection connection = getConnection();
             Statement statement = (Statement) connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                result.append("Username: ").append(resultSet.getString("username")).append(", ");
                result.append("Password: ").append(resultSet.getString("password")).append("\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error executing SELECT query: " + e.getMessage();
        }

        return result.toString();
    }

    private static String executeUpdate(String query) {
        try (Connection connection = getConnection();
             Statement statement = (Statement) connection.createStatement()) {

            int rowsAffected = statement.executeUpdate(query);

            if (query.toUpperCase().startsWith("INSERT")) {
                return rowsAffected > 0 ? "Data inserted successfully!" : "No data was inserted.";
            } else if (query.toUpperCase().startsWith("UPDATE")) {
                return rowsAffected > 0 ? "Data updated successfully!" : "No data was updated.";
            } else if (query.toUpperCase().startsWith("DELETE")) {
                return rowsAffected > 0 ? "Data deleted successfully!" : "No data was deleted.";
            } else {
                return "Unknown operation.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error executing operation: " + e.getMessage();
        }
    }

}