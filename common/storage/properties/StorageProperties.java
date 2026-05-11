package myproject.common.storage.properties;

import lombok.Data;
import myproject.common.storage.enums.StorageTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 存储配置项
 */
@Data
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {
	/**
	 * 是否开启存储
	 */
	private boolean enabled;
	/**
	 * 通用配置项
	 */
	private StorageConfig config;
	/**
	 * 本地配置项
	 */
	private LocalStorageProperties local;

	@Data
	public static class StorageConfig {
		/**
		 * 访问域名
		 */
		private String domain;
		/**
		 * 配置路径前缀
		 */
		private String prefix;
		/**
		 * 存储类型
		 */
		private StorageTypeEnum type;
	}

	@Bean
	@ConfigurationProperties(prefix = "storage.local")
	public LocalStorageProperties localStorageProperties() {
		return new LocalStorageProperties();
	}

}