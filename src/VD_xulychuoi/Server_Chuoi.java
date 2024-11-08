package VD_xulychuoi;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

public class Server_Chuoi {
         public static void main(String[] args) throws Exception {
        	 DatagramSocket serverSocker=new DatagramSocket(9898);
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
    			 String chuoiHoa=request.toUpperCase();
    			 String vuahoavuathuong=swapCase(request);
    			 int sotu=countWords(request);
    			 String str="Chuoi hoa:"+chuoiHoa+"\n"+"Chuoi thuong"+vuahoavuathuong+"\n"+"So tu:"+sotu+"\n";
    			 sendData=str.getBytes();
    			 DatagramPacket sendPacket=new DatagramPacket(sendData,sendData.length,IPAddress,port);
    			 //ghi lai du lieu cho client
    			 serverSocker.send(sendPacket);
    		 }
		}
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

}
