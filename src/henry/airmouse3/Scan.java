package henry.airmouse3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

enum ErrorCode {
	Wifi_Off, Wifi_Not_Connected, Time_Out, None
};

interface OnScanInterruptedListener {
	public void IpFound(DatagramSocket socket);

	public void ErrorHappened(ErrorCode error);
}

public class Scan {
	public static int TIMEOUT = 30000;
	public static ErrorCode ERROR;
	private static OnScanInterruptedListener _listener;

	public static void SetOnScanInterrupted(OnScanInterruptedListener listener) {
		_listener = listener;
	}

	public DatagramSocket ScanIps(Context context) {
		return ScanIpsBlocking(context);
	}

	public static void ScanIpsAsync(final Context context) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				DatagramSocket ret = ScanIpsBlocking(context);
				if (ret == null) {
					_listener.ErrorHappened(ERROR);
				} else {
					_listener.IpFound(ret);
				}
			}
		});
		t.start();
	}

	private static DatagramSocket ScanIpsBlocking(Context context) {

		ERROR = ErrorCode.None;
		DatagramSocket ret = null;
		long startScan = new Date().getTime();

		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo infoWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!infoWifi.isConnected()) {

			ERROR = ErrorCode.Wifi_Not_Connected;
			return null;
		}

		WifiManager wim = (WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE);
		int ipAd = wim.getConnectionInfo().getIpAddress();

		final String ipString = String.format(Locale.US, "%d.%d.%d.%d", (ipAd & 0xff), (ipAd >> 8 & 0xff), (ipAd >> 16 & 0xff), (ipAd >> 24 & 0xff));
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
			return null;

		for (int i = 1; i <= 254; i++) {
			long now = new Date().getTime();
			if (now - startScan > TIMEOUT) {
				ERROR = ErrorCode.Time_Out;
				ret = null;
			}
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
				socket.setSoTimeout(500);
				socket.connect(new InetSocketAddress(address, Settings.getPORT()));

				byte[] bytes = "hola".getBytes("UTF-8");
				DatagramPacket packetSend = new DatagramPacket(bytes, bytes.length);

				byte[] data = new byte[7];
				DatagramPacket packet = new DatagramPacket(data, data.length);

				socket.send(packetSend);

				socket.receive(packet);

				String received = new String(data, "UTF-8");
				
				if (received.equals("que ase"))
					ret = socket;

			} catch (Exception e) {
				if (socket != null) {
					if (socket.isConnected())
						socket.disconnect();
					
					socket.close();
				}

			} finally {

				if (ret != null)
					break;

				else if (i == 253)
					i = 0;
			}

		}
		return ret;
	}

	public static String GetCurrentSsid(Context context) {
		String ssid = "No NetWork";
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo.isConnected()) {
			final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
			if (connectionInfo != null && !connectionInfo.getSSID().equals("")) {
				ssid = connectionInfo.getSSID();
			}
		}
		return ssid;
	}
}