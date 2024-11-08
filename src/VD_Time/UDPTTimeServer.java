package VD_Time;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Date;

public class UDPTTimeServer {
       public static void main(String[] args) throws Exception {
		 DatagramSocket serverSocker=new DatagramSocket(9876);
		 System.out.println("Server is started");
		 byte[] receiveData=new byte[1024];
		 byte[] sendData=new byte[1024];
		 while(true) {
			 //tao goi rong de nhan du lieu tu client
			 DatagramPacket receivePacket=new DatagramPacket(receiveData, receiveData.length);
			 //nhan du lieu tu client
			 serverSocker.receive(receivePacket);
			 //lay dia chi ip cua client
			 InetAddress IPAddress=receivePacket.getAddress();
			 //lay port cua client
			 int port=receivePacket.getPort();
			 //lay ngay gio de gui nguoc lai client
			 String request=new String(receivePacket.getData());
			 if(request.trim().equals("getDate")){
				 sendData=new Date().toString().getBytes();
			 }
			 else {
				 sendData="Server don't know what do you want".getBytes();
			 }
			 DatagramPacket sendPacket=new DatagramPacket(sendData,sendData.length,IPAddress,port);
			 //ghi lai du lieu cho client
			 serverSocker.send(sendPacket);
		 }
	  }
}
