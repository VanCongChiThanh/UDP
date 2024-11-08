package VD_Time;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPTimeCLient {
       public static void main(String[] args) throws Exception {
		DatagramSocket clienSocket=new DatagramSocket();
		InetAddress IPAddress=InetAddress.getByName("localhost");
		byte[] sendData=new byte[1024];
		byte[] receiveData=new byte[1024];
		sendData="getDate".getBytes();
		
		DatagramPacket sendPacket=new DatagramPacket(sendData, sendData.length,IPAddress,9876);
		clienSocket.send(sendPacket);
		DatagramPacket receivePacket=new DatagramPacket(receiveData, receiveData.length);
		
		clienSocket.receive(receivePacket);
		String str=new String(receivePacket.getData(),0,receivePacket.getLength());
		clienSocket.close();
		System.out.print(str);
	}
}
