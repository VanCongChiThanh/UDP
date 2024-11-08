package VD_xulychuoi;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class Client_Chuoi {
   public static void main(String[] args) throws Exception {
		DatagramSocket clienSocket=new DatagramSocket();
		InetAddress IPAddress=InetAddress.getByName("localhost");
		byte[] sendData=new byte[1024];
		byte[] receiveData=new byte[1024];
        Scanner in = new Scanner(System.in);
        System.out.print("Nhap chuoi:");
        
        // Reading input from the user
        String userInput = in.nextLine();
		sendData=userInput.getBytes();
		
		DatagramPacket sendPacket=new DatagramPacket(sendData, sendData.length,IPAddress,9898);
		clienSocket.send(sendPacket);
		DatagramPacket receivePacket=new DatagramPacket(receiveData, receiveData.length);
		
		clienSocket.receive(receivePacket);
		String str=new String(receivePacket.getData(),0,receivePacket.getLength(),"UTF-8");
		clienSocket.close();
		System.out.print(str);
		
}
}
