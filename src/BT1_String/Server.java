package BT1_String;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


public class Server extends JFrame {

	private JPanel contentPane;
    private JTextField textField;
    private JTextArea textArea;
    private DatagramSocket serverSocket;
	/**
	 * Launch the application.
	 */
    private static String swapCase(String input) {
        StringBuilder swapped = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                swapped.append(Character.toLowerCase(c));
            } else if (Character.isLowerCase(c)) {
                swapped.append(Character.toUpperCase(c));
            } else {
                swapped.append(c);
            }
        }
        return swapped.toString();
    }

    private static int countWords(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }
        String[] words = input.trim().split("\\s+");
        return words.length;
    }

    private static int countVowels(String input) {
        int count = 0;
        String vowels = "aeiouAEIOU";
        for (char c : input.toCharArray()) {
            if (vowels.indexOf(c) != -1) {
                count++;
            }
        }
        return count;
    }
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
	/**
	 * Create the frame.
	 */
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
                                textArea.append("\nServer nhận chuỗi: " + request);
                                InetAddress clientIPAddress = receivePacket.getAddress();
                                int clientPort = receivePacket.getPort();

                                String message = "Chu hoa: " + request.toUpperCase() + "\n" +
                                        "Chu thuong: " + request.toLowerCase() + "\n" +
                                        "Chu vua hoa vua thuong: " + swapCase(request) + "\n" +
                                        "Chuoi dao nguoc: " + new StringBuilder(request).reverse() + "\n" +
                                        "So tu: " + countWords(request) + "\n" +
                                        "So nguyen am: " + countVowels(request) + "\n";
                                message = message + "\n";
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

}