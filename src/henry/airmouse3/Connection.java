package henry.airmouse3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class Connection {

	private static DatagramSocket _socket = null;

	protected static void CreateConnection(DatagramSocket socket) {

		_socket= socket;
	}

	protected synchronized static void Send(String str) {
		if (_socket != null && _socket.isConnected() && !_socket.isClosed()) {
			try {
				byte[] bytes = str.getBytes("UTF-8");
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
				_socket.send(packet);

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	public static boolean IsConnected() {
		if (_socket == null)
			return false;
		return _socket.isConnected();
	}

	protected static void Disconnect() {
		if (_socket == null)
			return;
		if (_socket.isConnected()) {
			Send("adeu");
			//_socket.disconnect();
		}
		_socket.close();

	}
}
