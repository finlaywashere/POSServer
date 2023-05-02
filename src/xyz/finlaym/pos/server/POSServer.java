package xyz.finlaym.pos.server;

import java.io.File;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import xyz.finlaym.pos.server.connector.DataConnection;
import xyz.finlaym.pos.server.connector.SQLConnector;

public class POSServer {
	public static void main(String[] args) throws Exception {
		DataConnection connection = new SQLConnector();
		File socket = new File("/src/http/pos.sock");
		if(socket.exists()) {
			socket.delete();
		}
		UnixDomainSocketAddress address = UnixDomainSocketAddress.of(socket.toPath());
		ServerSocketChannel ss = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
		ss.configureBlocking(false);
		ss.bind(address);
		Set<PosixFilePermission> perms = Files.readAttributes(socket.toPath(),PosixFileAttributes.class).permissions();

        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        perms.add(PosixFilePermission.OTHERS_WRITE);
        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);
        Files.setPosixFilePermissions(socket.toPath(), perms);
        
		while(ss.isOpen()) {
			try {
				SocketChannel s = ss.accept();
				if(s != null)
					new SocketHandler(connection, s);
				Thread.sleep(50);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
