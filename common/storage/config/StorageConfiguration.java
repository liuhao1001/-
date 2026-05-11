package myproject.common.storage.config;


import myproject.common.storage.properties.StorageProperties;
import myproject.common.storage.service.LocalStorageService;
import myproject.common.storage.service.StorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 存储配置文件
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnProperty(prefix = "storage", value = "enabled")
public class StorageConfiguration {

	@Bean
	public StorageService storageService(StorageProperties properties) {
		return new LocalStorageService(properties);
	}

}