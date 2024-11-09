package BT1_String;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Client extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextArea textArea;
	public DatagramSocket clientSocket = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws SocketException 
	 */
	public Client() throws SocketException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 481);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("PORT");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(24, 26, 45, 13);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(92, 13, 160, 34);
		contentPane.add(textField);
		textField.setColumns(10);
		clientSocket = new DatagramSocket();
		
		textArea = new JTextArea();
		textArea.setBounds(24, 57, 389, 253);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(24, 57, 389, 253);
		contentPane.add(scrollPane);
		
		JLabel lblNewLabel_1 = new JLabel("Nhập chuỗi");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(24, 320, 125, 22);
		contentPane.add(lblNewLabel_1);
		
		textField_1 = new JTextField();
		textField_1.setBounds(24, 343, 389, 34);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnNewButton_1 = new JButton("SEND");
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnNewButton_1.setBounds(164, 388, 110, 34);
		contentPane.add(btnNewButton_1);
		btnNewButton_1.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						new Thread(new Runnable() {
		                    @Override
		                    public void run() {
		                        InetAddress IPAddress = null;
		                        try {
		                            IPAddress = InetAddress.getByName("localhost");
		                        } catch (UnknownHostException e1) {
		                            e1.printStackTrace();
		                        }
		                            byte[] sendData = new byte[65507];
		                            byte[] receiveData = new byte[65507];
		                            String sentence = textField_1.getText();
		                            sendData = sentence.getBytes();
		                            int port = Integer.parseInt(textField.getText());
		                            DatagramPacket sendPacket =
		                                    new DatagramPacket(sendData, sendData.length, IPAddress, port);

		                            try {
		                                clientSocket.send(sendPacket);
		                            } catch (IOException e1) {
		                                e1.printStackTrace();
		                            }

		                            DatagramPacket receivePacket =
		                                    new DatagramPacket(receiveData, receiveData.length);

		                            try {
		                                clientSocket.receive(receivePacket);
		                            } catch (IOException e1) {
		                                e1.printStackTrace();
		                            }


		                            String modifiedSentence = new String(receivePacket.getData());
		                            textArea.append("FROM SERVER:" + "\n" + modifiedSentence);
		                    }
		                }).start();
						
					}
			
				});
	}

}