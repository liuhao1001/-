package myproject.common.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 */
@Slf4j
@Service
@Scope("singleton")
public class AppContext implements InitializingBean, DisposableBean {

	@Resource
	private WebSocketServer webSocketServer;
	private Thread nettyThread;

	/**
	 * 服务器关闭前调用
	 *
	 * @throws Exception
	 */
	@Override
	public void destroy() throws Exception {
		log.info("正在释放Netty Websocket相关连接...");
		webSocketServer.close();
		log.info("正在关闭Netty Websocket服务器线程...");
		nettyThread.interrupt();
		log.info("系统成功关闭！");
	}

	/**
	 * 服务器启动后调用
	 *
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		nettyThread = new Thread(webSocketServer);
		log.info("开启独立线程，启动Netty WebSocket服务器...");
		nettyThread.start();
	}
}
