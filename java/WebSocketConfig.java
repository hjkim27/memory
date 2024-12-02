package sample.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.concurrent.TimeUnit;


/**
 * Socket통신을 위한 Config관련 클래스
 * @author hjkim
 * @date 2023.04.12
 */
@Slf4j
@Configuration
public class WebSocketConfig extends ServerEndpointConfig.Configurator implements ApplicationContextAware {

    /**
     *  '@ServerEndpoint'가 붙은 클래스는 'DI(@Autowired, @Resource, @Inject, @RequiredArgsConstructor)'가 설정된 멤버가 정상적으로 초기화되지 않음
     *   BeanFactory 또는 ApplicationContext 를 연결해줘야 함.
     */
    private static volatile BeanFactory context;

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return context.getBean(endpointClass);
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        WebSocketConfig.context = applicationContext;
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        // HttpRequest로부터 Session을 받는다.
        HttpSession session = (HttpSession) request.getHttpSession();
        // HttpSession으로 부터 Context도 받는다.
        ServletContext context = session.getServletContext();
        config.getUserProperties().put(HttpSession.class.getName(), session);
        config.getUserProperties().put("Context", context);
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(10000000);
        container.setMaxBinaryMessageBufferSize(10000000);
        // TODO 10초이상 소켓이 안오면 죽도록 해뒀는데 동작하는지는 모르겠음. 확인 필요함.
        container.setMaxSessionIdleTimeout(TimeUnit.SECONDS.toMillis(10));
        return container;
    }
}
