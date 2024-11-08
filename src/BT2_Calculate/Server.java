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
	/**
	 * Launch the application.
	 */
    public String calculate(String s) {
        if (s == null || s.length() == 0) {
            return "0";
        }

        s = s.trim().replaceAll("[ ]+", "");

        if (s == null || s.length() == 0) {
            return "0";
        }

        Stack<Character> opStack = new Stack<>();
        Stack<Integer> numStack = new Stack<>();

        int i = 0;
        while (i < s.length()) {
            if (Character.isDigit(s.charAt(i))) {
                int num = 0;
                while (i < s.length() && Character.isDigit(s.charAt(i))) {
                    num = num * 10 + Character.getNumericValue(s.charAt(i));
                    i++;
                }
                numStack.push(num);
            } else {
                char op = s.charAt(i);
                if (opStack.isEmpty()) {
                    opStack.push(op);
                    i++;
                } else if (op == '+' || op == '-') {
                    char top = opStack.peek();
                    if (top == '(') {
                        opStack.push(op);
                        i++;
                    } else {
                        calculate(numStack, opStack);
                    }
                } else if (op == '*' || op == '/') {
                    char top = opStack.peek();
                    if (top == '(') {
                        opStack.push(op);
                        i++;
                    } else if (top == '*' || top == '/') {
                        calculate(numStack, opStack);
                    } else if (top == '+' || top == '-') {
                        opStack.push(op);
                        i++;
                    }
                } else if (op == '(') {
                    opStack.push(op);
                    i++;
                } else if (op == ')') {
                    while (opStack.peek() != '(') {
                        calculate(numStack, opStack);
                    }
                    opStack.pop();
                    i++;
                }
            }
        }

        while (!opStack.isEmpty()) {
            calculate(numStack, opStack);
        }

        String res = Integer.toString(numStack.peek());
        return res;
    }

    private void calculate(Stack<Integer> numStack, Stack<Character> opStack) {
        int num2 = numStack.pop();
        int num1 = numStack.pop();

        char op = opStack.pop();

        int ans = 0;

        switch(op) {
            case '+':
                ans = num1 + num2;
                break;
            case '-':
                ans = num1 - num2;
                break;
            case '*':
                ans = num1 * num2;
                break;
            case '/':
                ans = num1 / num2;
                break;
        }

        numStack.push(ans);
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
        setBounds(100, 100, 486, 548);
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
        btnNewButton.setBounds(361, 10, 103, 38);
        contentPane.add(btnNewButton);
        
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port = Integer.parseInt(textField.getText());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
                            textArea.append("Server is started \n");
                            while (true) {
                                byte[] receiveData = new byte[65507];
                                byte[] sendData = new byte[65507];
                                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                                serverSocket.receive(receivePacket);
                                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());

                                InetAddress clientIPAddress = receivePacket.getAddress();
                                int clientPort = receivePacket.getPort();

                                String message = calculate(request);
                                message = message + "\n";
                                sendData = message.getBytes();
                                textArea.append(message);
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
        textArea.setBounds(10, 127, 454, 343);
        contentPane.add(textArea);
	}

}