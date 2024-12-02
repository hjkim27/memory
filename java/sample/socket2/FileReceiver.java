package sample.socket2;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

public class FileReceiver extends Thread {

	private Logger log = Logger.getLogger(getClass());
	
	Socket socket;
	DataInputStream dis;
	FileOutputStream fos;
	BufferedOutputStream bos;
	
	String filePath;
	
	public FileReceiver(Socket socket, String filePath) {
		this.socket = socket;
		this.filePath = filePath;
	}
	
	@Override
	public void run() {
		try {
			dis = new DataInputStream(socket.getInputStream());
			String type = dis.readUTF();
			
			if(type.equals("file")) {
				String result = fileWrite(dis);
				log.debug("result : "+ result);
			}
			
		} catch (Exception e) {
			log.error("run() Fail", e);
		}
	}

	private String fileWrite(DataInputStream dis) {
		
		String result;
		
		try {
			log.info("Start file receive...");
			
			String fileName = dis.readUTF();
			log.info("ReceiveFile :: "+ fileName);
			
			File file = new File(filePath + "/tmp" + File.separator + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			
			int len;
			int size = 4096;
			byte[] data = new byte[size];
			while((len = dis.read(data))!= -1) {
				bos.write(data, 0, len);
			}
			
			result = "success";
			
			log.info("success receive file, size : "+ file.length());
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = "error";
			
		} finally {
			try {
				if(bos!= null) {
					bos.close();
				}
				if(fos!= null) {
					fos.close();
				}
				if(dis!= null) {
					dis.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		
		return result;
	}
}

























