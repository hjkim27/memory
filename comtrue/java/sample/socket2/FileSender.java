package sample.api.socket2;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

public class FileSender extends Thread {
	
	private Logger log = Logger.getLogger(getClass());
	
	String filePath;
	String fileName;
	Socket socket;
	DataOutputStream dos;
	FileInputStream fis;
	BufferedInputStream bis;
	
	public FileSender(Socket socket, String filePath, String fileName) {
		this.socket = socket;
		this.fileName = fileName;
		this.filePath = filePath;
		
		try {
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void run() {
		try {
			dos.writeUTF("file");
			dos.flush();
			
			String result = fileRead(dos);
			log.info("result : "+ result);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if(dos!= null) {
					dos.close();
				}
				if(bis!= null) {
					bis.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private String fileRead(DataOutputStream dos) {
		String result;
		
		try {
			dos.writeUTF(fileName);
			log.info("SendFile :: "+ fileName);
			
			File file = new File(filePath + File.separator + fileName);
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			
			int len;
			int size = 4096;
			byte[] data = new byte[size];
			while((len = bis.read(data))!= -1) {
				dos.write(data, 0, len);
			}
			
			dos.flush();
			
			result = "success";
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = "error";
		} finally {
			try {
				if(fis!= null) {
					fis.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return result;
	}
}


