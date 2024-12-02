package sample.api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@slf4j
public class DataApi {

	private static final String BOUNDARY = "WebKitFormBoundary7MA4YWxkTrZu0gW";
	private static final String LINE = "\r\n";
	
	static String result = "";
	
	public String run(String url, List<File> fileList) throws IOException {
		return run(url, BOUNDARY, fileList);
	}

	/**
	 * <pre>
	 *   HttpUrlConnection을 사용한 restAPI
	 * </pre>
	 * 
	 * @param url 		api url
	 * @param boundary	구분자
	 * @param fileList	전달 파일 목록
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String run(String url, String boundary, List<File> fileList) throws MalformedURLException, IOException {

		/* HttpURLConnection생성 및 설정 */
		URLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		((HttpURLConnection) conn).setRequestMethod("POST");
		conn.setUseCaches(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setConnectTimeout(10000);
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+ boundary);
		conn.setRequestProperty("Cache-Control", "no-cache");
		/* 설정 끝 --------------------- */

		try (
			OutputStream out = conn.getOutputStream();
		){
			/* 이미지 첨부 시작 --- */
			try(
				OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
				PrintWriter writer = new PrintWriter(outWriter, true)
			){
				for(File file :  fileList){
					addFile(file);
				}
				writer.append("--").append(boundary).append("--").append(LINE);
			}
			/* 이미지첨부 끝 ---- */

			// 응답확인 -------------
			int responseCode = ((HttpURLConnection) conn).getResponseCode();
			try(
				InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
				BufferedReader br = new BufferedReader(isr);
			){
				String inputLine = "";
				StringBuffer responseStr = new StringBuffer();
				while((inputLine = reader.readLine())!= null) {
					responseStr.append(inputLine);
				}
				result = responseStr.toString();
			}
			// 응답확인 끝 -------------

		} catch(Exception e){
			log.error(e.getMessage(), e);
		}
		return result;
	}
    
    /**
	 * <pre>
	 * 	파일 추가 메서드 분리
	 * </pre>
	 * 
	 * @param boundary
	 * @param _file
	 * @throws IOException
	 */
    private static void addFile(String boundary, File _file) throws IOException{// Send File
    	if(!_file.exists()) {
    		log.info("file does not exist!!!!!!");
    	} else {
    		log.info(_file.getPath());
    	}
        writer.append("--").append(boundary).append(LINE);
        writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"" + _file.getName() + "\"").append(LINE);
        writer.append("Content-Type: image/png").append(LINE);
        writer.append("Content-Transfer-Encoding: binary").append(LINE);
        writer.append(LINE);
        writer.flush();
         
		try (
			FileInputStream inputStream = new FileInputStream(_file);
		) {
			byte[] buffer = new byte[(int)_file.length()];
			int bytesRead = -1;
			while((bytesRead = inputStream.read(buffer)) != -1){
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
		}
        writer.append(LINE).flush();
    }
}
