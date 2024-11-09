package BT2_Calculate;
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
    public static double calculateExpression(String expression) throws Exception {
        char[] tokens = expression.toCharArray();
        Stack<Double> values = new Stack<>(); 
        Stack<Character> operators = new Stack<>(); 

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ') {
                continue;
            }
            if (Character.isDigit(tokens[i])) {
                StringBuilder sb = new StringBuilder();
                while (i < tokens.length && (Character.isDigit(tokens[i]) || tokens[i] == '.')) {
                    sb.append(tokens[i++]);
                }
                values.push(Double.parseDouble(sb.toString()));
                i--; 
            }
            else if (tokens[i] == '(') {
                operators.push(tokens[i]);
            }
            else if (tokens[i] == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop(); 
            }
            else if (isOperator(tokens[i])) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(tokens[i])) {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(tokens[i]);
            }
        }
        while (!operators.isEmpty()) {
            values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private static double applyOperation(char operator, double b, double a) throws Exception {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) throw new Exception("Không thể chia cho 0");
                return a / b;
        }
        return 0;
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
        }
        return 0;
    }
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
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
                                textArea.append("\nServer nhận phép toán: " + request);
                                InetAddress clientIPAddress = receivePacket.getAddress();
                                int clientPort = receivePacket.getPort();

                                String message = null;
								try {
									message = String.valueOf( calculateExpression(request))+"\n";
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

}