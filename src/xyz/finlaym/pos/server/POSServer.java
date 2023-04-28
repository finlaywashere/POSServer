package xyz.finlaym.pos.server;

import java.io.File;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import xyz.finlaym.pos.server.connector.DataConnection;
import xyz.finlaym.pos.server.connector.SQLConnector;

public class POSServer {
	public static void main(String[] args) throws Exception {
		DataConnection connection = new SQLConnector();
		File socket = new File("pos.sock");
		if(socket.exists()) {
			socket.delete();
		}
		UnixDomainSocketAddress address = UnixDomainSocketAddress.of(socket.toPath());
		ServerSocketChannel ss = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
		ss.configureBlocking(false);
		ss.bind(address);
		while(ss.isOpen()) {
			try {
				SocketChannel s = ss.accept();
				if(s != null)
					new SocketHandler(connection, s);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
