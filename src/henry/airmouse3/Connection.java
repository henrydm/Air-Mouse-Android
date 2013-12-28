package henry.airmouse3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class Connection {
	
	private static int SERVERPORT = 6000;
	private static String SERVER_IP = "hfgjhgfgh";
	private static DatagramSocket _socket=null;
	
	protected static void CreateConnection(String ip, int port)
	{
		SERVERPORT = port;
		SERVER_IP = new String(ip);
		
		if (_socket!=null)
			_socket.close();
		
		try {
			_socket = new DatagramSocket();
			_socket.connect(new InetSocketAddress(SERVER_IP, SERVERPORT));
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	protected synchronized static void Send(String str)
	{
		try {
			byte[] bytes = str.getBytes();
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
			_socket.send(packet);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	
	
	
}
