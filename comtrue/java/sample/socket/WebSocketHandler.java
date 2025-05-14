package sample.api.socket;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import sample.WebSocketConfig;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * 소켓 통신을 위한 Handler 클래스
 *
 * @author hjkim
 */
@Slf4j
@Component
@ServerEndpoint(value = "/socketConnection", configurator = WebSocketConfig.class)
@NoArgsConstructor
public class WebSocketHandler {

    private static Set<Session> CLIENTS = Collections.synchronizedSet(new HashSet<>());

    private Gson GSON = new Gson();

    /**
     * MultipartFile과 함께 보낼 데이터 저장
     * <ul>
     *     <li>key : sessionId</li>
     *     <li>value : socket통신으로 보내온 StringMessage</li>
     * </ul>
     */
    private Map<String, Map<String, Object>> dataMap = new HashMap<>();

    /*
     * 소켓통신 연결
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        if (CLIENTS.contains(session)) {
            log.debug("{} | {} >>> already connected....", session.getId(), session.toString());
        } else {
            CLIENTS.add(session);
            log.debug("{} | {} >>> new connection!!", session.getId(), session.toString());
        }
    }

    /*
     * 소켓통신 종료
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        CLIENTS.remove(session);
        dataMap.remove(session.getId());
        log.debug("{} | {} >>> session closed", session.getId(), session.toString());
    }

    /*
     * ByteBuffer 데이터 송/수신
     * 이미지파일 저장 및 OCR을 위한 apiProxyService 호출
     * @param buffer
     * @param session
     * @throws IOException
     */
    @OnMessage
    public void onMessage(ByteBuffer buffer, Session session) {
        byte[] metadata = buffer.array();

        String sessionId = session.getId();
        if (dataMap.get(sessionId) != null) {
            Map<String, Object> map = dataMap.get(sessionId);
            log.debug("{} >>> size : {} | {}", sessionId, dataMap.size(), GSON.toJson(map));
            String token = String.valueOf(map.get("token") == null ? "" : map.get("token"));
            String resBody = "";
            if (token != null && !token.isEmpty() && !token.equals("")) {
                try {
                    resBody = parseSocketRequest(token, metadata, map, session.getRequestURI().toString());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    session.getAsyncRemote().sendText(e.getMessage());
                }
                session.getAsyncRemote().sendText(resBody);
            }
        } else {
            session.getAsyncRemote().sendText("sessionId not exist [ " + sessionId + " ]");
            log.warn("{} | {} >>> sessionId not exist", sessionId, session.toString());
        }
    }


    /*
     * 텍스트데이터 송신
     * OCR 진행 시 함께 사용할 데이터 저장
     * @param message
     * @param session
     */
    @OnMessage
    public void onTextMessage(String message, Session session) {
        try {
            JsonReader reader = new JsonReader(new StringReader(message));
            reader.setLenient(true);
            Map<String, Object> map = GSON.fromJson(reader, Map.class);
//            log.info("{} | {} >>> {}", session.getId(), map.get("token"), GSON.toJson(map));        // only use the develop check
            log.debug("{} | {} >>> dataMap put keys :: {}", session.getId(), map.get("token"), map.keySet());

            String key = "maxCount";
            if (map.containsKey(key)) {
                String value = String.valueOf(map.get(key));
                value = value.split("\\.")[0];
                map.put(key, value);
            }
            dataMap.put(session.getId(), map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /*
     * 소켓 통신 중 에러 발생 시 호출, 커넥션이 종료됨.
     * @param session
     * @param t
     * @throws IOException
     */
    @OnError
    public void error(Session session, Throwable t) throws IOException {
        if (t.getMessage().equals(EOFException.class)) {
            log.debug("{} >>> browser Exit.", session.getId());
        }
        log.error("{} >>> {} | {}", session.getId(), t.getMessage(), t);
        session.getAsyncRemote().sendText("exception!!!");
    }

    // TODO 동작하는지 확인 필요
    @OnMessage
    public void onPong(PongMessage message, Session session) {
        try {
            long time = message.getApplicationData().getLong();
            long delay = System.currentTimeMillis() - time;
            if (delay > 200) {
                log.info("{} >>> delay timeout 200 over : {}", session.getId(), System.currentTimeMillis() - time);
            }
            session.getBasicRemote().sendPong(message.getApplicationData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseSocketRequest(String token, byte[] fileByte, Map<String, Object> paramMap, String serverName) {

        long start_time = System.currentTimeMillis();
        long end_time = -1;

        MultipartFile file = new MockMultipartFile("file.jpg", fileByte);       //socket image
        //socket datas
        
        log.info("parseSocketRequest: {} | {}", pathVariable, GSON.toJson(paramMap));

        Map<String, Object> map = new HashMap<>();
        /**
         * socket 데이터 관련 무언가...
         */
        return GSON.toJson(map);
    }

}
