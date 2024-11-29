package sample.api;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Slf4j
public class DataApiSample {


	// 결과값을 받을 변수
	String result = "";

	/**
	 *
	 * @param url 		호출url		http://192.168.100.144:8096/api/v3/imei/ocr
	 * @param boundary	boundary	WebKitFormBoundary7MA4YWxkTrZu0gW
	 * @param fileList	첨부이미지목록
	 * @throws IOException
	 */
	public void run(String url, String boundary, List<File> fileList) throws IOException {

		/* HttpURLConnection생성 및 설정 */
		URLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		((HttpURLConnection) conn).setRequestMethod("POST");
		conn.setUseCaches(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setConnectTimeout(10000);
		conn.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
		conn.setRequestProperty("Cache-Control", "no-cache");
		/* 설정 끝 --------------------- */

		OutputStream outputStream = conn.getOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

		/* 이미지 첨부 시작 --- */
		try {
			for (File file : fileList) {
				writer.append("--" + boundary).append("\r\n");
				writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"" + file.getName() + "\"").append("\r\n");
				writer.append("Content-Type: image/jpeg").append("\r\n");
				writer.append("\r\n");
				writer.flush();

				FileInputStream inputStream = new FileInputStream(file);
				byte[] buffer = new byte[(int) file.length()];
				int bytesRead = -1;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.flush();
				inputStream.close();
				writer.append("\r\n");
				writer.flush();
			}

			writer.append("--" + boundary + "--").append("\r\n");
			writer.close();
		} catch (Exception e){
			log.error(e.getMessage());
		}
		/* 이미지첨부 끝 ---- */

		int responseCode = ((HttpURLConnection) conn).getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine = "";
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		try {
			result = response.toString();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
