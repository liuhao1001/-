package myproject.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.List;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
	private String getbehindDistPath() {
		String currentProjectPath = System.getProperty("user.dir");
		File currentDir = new File(currentProjectPath);
		File parentDir = currentDir.getParentFile();
		String behindDistPath = parentDir.getAbsolutePath() + File.separator + "behind" + File.separator + "dist" + File.separator;
		System.out.println(behindDistPath);
		return behindDistPath;
	}

	private String getfrontDistPath() {
		String currentProjectPath = System.getProperty("user.dir");
		File currentDir = new File(currentProjectPath);
		File parentDir = currentDir.getParentFile();
		String frontDistPath = parentDir.getAbsolutePath() + File.separator + "front" + File.separator + "dist" + File.separator;
		System.out.println(frontDistPath);
		return frontDistPath;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 静态资源访问路径
		registry.addResourceHandler("/**")
				// classpath下的静态资源目录
				.addResourceLocations("classpath:/resources/")
				.addResourceLocations("classpath:/public/")
				.addResourceLocations("classpath:/upload/")
				.addResourceLocations("classpath:/static/")
				.addResourceLocations("file:" + getbehindDistPath())
				.addResourceLocations("file:" + getfrontDistPath())
				.addResourceLocations("classpath:/front/dist/")
				.addResourceLocations("classpath:/behind/dist/");
		WebMvcConfigurer.super.addResourceHandlers(registry);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOriginPatterns("*") // 允许的前端地址
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> list) {
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = jsonConverter.getObjectMapper();
		//序列换成json时,将所有的long变成string
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
		simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
		objectMapper.registerModule(simpleModule);
		list.add(jsonConverter);
	}
}
