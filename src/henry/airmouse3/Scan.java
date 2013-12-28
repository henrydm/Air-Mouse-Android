package henry.airmouse3;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

enum ErrorCode {
	Wifi_Off, Wifi_Not_Connected, None
};
interface OnScanInterruptedListener {
    public void IpFound(String ip);
    public void ErrorHappened(ErrorCode error);
}

public class Scan {

	public static ErrorCode ERROR;
	private static List<OnScanInterruptedListener> _listeners = new ArrayList<OnScanInterruptedListener>();
	
	public static void SetOnScanInterrupted(OnScanInterruptedListener listener)
	{
		_listeners.add(listener);
	}

	public static String ScanIps(Context context) 
	{
		return ScanIpsBlocking(context);
	}
	
	public static void ScanIpsAsync(final Context context) 
	{
		Thread t = new Thread(new Runnable() {
	         public void run()
	         {
	        	 String ret = ScanIpsBlocking(context);
	        	 if (ret=="error" || ret == "")
	        	 {
	        		 for (OnScanInterruptedListener listener : _listeners)
	        			 listener.ErrorHappened(ERROR);
	        	 }
	        	 else
	        	 {
	        		 for (OnScanInterruptedListener listener : _listeners)
	        			 listener.IpFound(ret);
	        	 }
	         }
	});
		 t.start();
	}
	
	private static String ScanIpsBlocking(Context context) {

		ERROR = ErrorCode.None;
		String ret = "";

		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo infoWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!infoWifi.isConnected()) {

			ERROR = ErrorCode.Wifi_Not_Connected;
			return "error";
		}

		WifiManager wim = (WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE);
		int ipAd = wim.getConnectionInfo().getIpAddress();

		final String ipString = String.format(Locale.US,"%d.%d.%d.%d", (ipAd & 0xff), (ipAd >> 8 & 0xff), (ipAd >> 16 & 0xff), (ipAd >> 24 & 0xff));
		InetAddress ipAddres = null;
		byte[] ip = null;

		try {
			ipAddres = InetAddress.getByName(ipString);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		if (ipAddres != null)
			ip = ipAddres.getAddress();
		else
			return ret;

		for (int i = 1; i <= 254; i++) {

			ip[3] = (byte) i;
			InetAddress address = null;
			DatagramSocket socket = null;

			try {
				address = InetAddress.getByAddress(ip);
			} catch (UnknownHostException e1) {
				continue;
			}
			try {
				socket = new DatagramSocket();
				socket.connect(new InetSocketAddress(address, 6000));

				byte[] bytes = "hola".getBytes();
				DatagramPacket packetSend = new DatagramPacket(bytes, bytes.length);
				socket.send(packetSend);

				byte[] data = new byte[7];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				socket.setSoTimeout(100);
				socket.receive(packet);
				ret = address.getHostAddress();

			} catch (Exception e) {
				// Log.i("scan", "Adress Fail: " + address.getHostAddress());
				continue;

			} finally {
				if (socket != null) {
					socket.close();
				}

				if (ret != "")
					break;

				else if (i == 253)
					i = 0;
			}

		}
		return ret;
	}
}
