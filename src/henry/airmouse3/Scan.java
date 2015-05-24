/********************************************************************************************
*	Copyright(C) 2014  Enric del Molino 													*
*	http://www.androidairmouse.com															*
*	enricdelmolino@gmail.com																*
*																							*
*	This file is part of Air Mouse Client for Android.										*
*																							*
*   Air Mouse Client for Android is free software: you can redistribute it and/or modify	*
*   it under the terms of the GNU General Public License as published by					*
*   the Free Software Foundation, either version 3 of the License, or						*
*   (at your option) any later version.														*
*																							*
*   Air Mouse Client for Android is distributed in the hope that it will be useful,			*
*   but WITHOUT ANY WARRANTY; without even the implied warranty of							*
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the							*
*   GNU General Public License for more details.											*
*																							*
*   You should have received a copy of the GNU General Public License						*
*   along with Air Mouse Server for Android.  If not, see <http://www.gnu.org/licenses/>.	*
*********************************************************************************************/

package henry.airmouse3;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
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
			Socket testSocket = null;

			try {
				address = InetAddress.getByAddress(ip);
			} catch (UnknownHostException e1) {
				continue;
			}
			try {
				//Define test socket
				testSocket = new Socket(address, Settings.getPORT());
				testSocket.setSoTimeout(100);
				
				DataOutputStream outToServer = new DataOutputStream(testSocket.getOutputStream());  
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(testSocket.getInputStream()));
				
				byte[] bytes = Commands.GetConnectionString().getBytes("UTF-8");
				outToServer.write(bytes) ;
				
				
				String inStr = inFromServer.readLine();
	
				if (inStr.equals(Commands.HelloResponse))
				{
					ret = new DatagramSocket();
					ret.connect(new InetSocketAddress(address, Settings.getPORT()));
				}
				


			} catch (Exception e) {
				if (testSocket != null) {
//					if (testSocket.isConnected())
//						testSocket.disconnect();
					try {
						testSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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