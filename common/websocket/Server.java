package myproject.common.websocket;

public interface Server {
	/**
	 * 启动服务器
	 */
	void start();

	/**
	 * 关闭服务器
	 */
	void close();
}
