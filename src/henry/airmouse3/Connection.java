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

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.util.Log;

public class Connection {

	private static DatagramSocket _socket = null;

	protected static void CreateConnection(DatagramSocket socket) {

		_socket = socket;
	}

	protected synchronized static void Send(String str) {
		if (_socket != null && _socket.isConnected() && !_socket.isClosed()) {
			try {
				byte[] bytes = str.getBytes("UTF-8");
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
				_socket.send(packet);

			} catch (Exception e) {

				e.printStackTrace();
				Log.i("air", e.getMessage());
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
			Send(Commands.Bye);
			// _socket.disconnect();
		}
		_socket.close();

	}
}
