package sample.api.socket2;

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SocketUtil {
	
	static Logger log = Logger.getLogger(SocketUtil.class);
	private static final String filePath = "/usr/local/qrCode";
	private static int port = 8081;
	
	
	public static void send(String qrKey) {
		String serverIp = "127.0.0.1";
		Socket socket = null;
		
		try {
			socket = new Socket(serverIp, port);
			log.info("Connect Server --");
			
			FileSender fileSender = new FileSender(socket, filePath, qrKey);
			fileSender.start();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	
	public static void receive() {
		ServerSocket server = null;
		Socket socket = null;
		
		try {
			server = new ServerSocket(port);
			log.info("starting server");
			
			while(true) {
				log.info("ready...");
				
				socket = server.accept();
				log.info("connect Client --");
				
				FileReceiver receiver = new FileReceiver(socket, filePath);
				receiver.start();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		
	}
	
}
