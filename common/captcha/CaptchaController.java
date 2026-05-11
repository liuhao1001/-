package myproject.common.captcha;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import myproject.common.exception.ServerException;
import myproject.common.sms.SmsComponent;
import myproject.common.utils.RedisCache;
import myproject.common.utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 */
@RestController
@Slf4j
public class CaptchaController {
	public static final String CAPTCHA_CODE_KEY = "captcha_codes:";
	/**
	 * 验证码有效期（分钟）
	 */
	public static final Integer CAPTCHA_EXPIRATION = 2;
	//验证码是否开启
	@Value("${captcha.captchaEnabled}")
	private String captchaEnabled;
	// 验证码类型
	@Value("${captcha.captchaType}")
	private String captchaType;
	@Value("${spring.mail.username}")
	private String fromEmail;
	@Resource(name = "captchaProducer")
	private Producer captchaProducer;

	@Resource(name = "captchaProducerMath")
	private Producer captchaProducerMath;
	@Resource
	private JavaMailSender mailSender;
	@Resource
	private RedisCache redisCache;
	@Resource
	private SmsComponent smsComponent;

	@PostMapping("/captchaEnabled")
	public Result<Boolean> captchaEnabled() {
		return Result.ok(Boolean.parseBoolean(captchaEnabled));
	}

	/**
	 * 生成验证码
	 */
	@PostMapping("/captchaImage")
	@SaIgnore
	public Result getCode() throws IOException {
		Map<String, String> resultMap = new HashMap<>();
		Result result = Result.ok(resultMap);
		resultMap.put("captchaEnabled", captchaEnabled);
		if (!Boolean.parseBoolean(captchaEnabled)) {
			return result;
		}

		// 保存验证码信息
		String uuid = UUID.fastUUID().toString();
		String verifyKey = CAPTCHA_CODE_KEY + uuid;

		String capStr = null, code = null;
		BufferedImage image = null;

		// 生成验证码
		if ("math".equals(captchaType)) {
			String capText = captchaProducerMath.createText();
			capStr = capText.substring(0, capText.lastIndexOf("@"));
			code = capText.substring(capText.lastIndexOf("@") + 1);
			image = captchaProducerMath.createImage(capStr);
		} else if ("char".equals(captchaType)) {
			capStr = code = captchaProducer.createText();
			image = captchaProducer.createImage(capStr);
		}

		redisCache.setCacheObject(verifyKey, code, CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
		// 转换流信息写出
		FastByteArrayOutputStream os = new FastByteArrayOutputStream();
		try {
			ImageIO.write(image, "jpg", os);
		} catch (IOException e) {
			return Result.error(e.getMessage());
		}

		resultMap.put("uuid", uuid);
		resultMap.put("img", Base64.encode(os.toByteArray()));
		return result;
	}

	/**
	 * @return void
	 * @title: validateCaptcha
	 * @Description //校验验证码
	 * @Param [code, uuid]
	 **/
	public void validateCaptcha(String code, String uuid) {
		boolean captchaEnable = Boolean.parseBoolean(captchaEnabled);
		if (captchaEnable) {
			String verifyKey = CAPTCHA_CODE_KEY + StrUtil.removeAll(uuid, "");
			String captcha = redisCache.getCacheObject(verifyKey);
			redisCache.deleteObject(verifyKey);
			if (captcha == null) {
				throw new ServerException("验证码已过期");
			}
			if (!code.equalsIgnoreCase(captcha)) {
				throw new ServerException("验证码填写错误");
			}
		}
	}

	/**
	 * @return void
	 * @title: sendEmail
	 * @Description //发送邮箱验证码
	 * @Param [email]
	 **/
	public void sendEmail(String email) {
		SimpleMailMessage message = new SimpleMailMessage();
		Random random = new Random();
		int code = random.nextInt(899999) + 100000;
		message.setFrom(fromEmail);
		message.setTo(email);
		message.setSubject("系统验证码");
		message.setText("邮箱验证码为: " + code + " ,请勿发送给他人");
		try {
			mailSender.send(message);
			String verifyKey = CAPTCHA_CODE_KEY + email;
			redisCache.setCacheObject(verifyKey, String.valueOf(code), CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
			log.info("验证码邮件已发送。");
		} catch (Exception e) {
			log.error("发送验证码邮件时发生异常了！", e);
		}
	}

	/**
	 * @return void
	 * @title: sendSMS
	 * @Description //短信验证码
	 * @Param [phone]
	 **/
	public void sendSMS(String phone) {
		Random random = new Random();
		int code = random.nextInt(899999) + 100000;
		try {
			String verifyKey = CAPTCHA_CODE_KEY + phone;
			smsComponent.send(phone, String.valueOf(code));
			redisCache.setCacheObject(verifyKey, String.valueOf(code), CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
			log.info("验证码已发送。");
		} catch (Exception e) {
			log.error("发送验证码时发生异常了！", e);
		}
	}

	/**
	 * @return void
	 * @title: validateSmsCaptcha
	 * @Description //校验短信验证码
	 * @Param [code, phone]
	 **/
	public void validateSmsCaptcha(String code, String phone) {
		String verifyKey = CAPTCHA_CODE_KEY + phone;
		String captcha = redisCache.getCacheObject(verifyKey);
		redisCache.deleteObject(verifyKey);
		if (captcha == null) {
			throw new ServerException("验证码已过期");
		}
		if (!code.equalsIgnoreCase(captcha)) {
			throw new ServerException("验证码填写错误");
		}
	}

	/**
	 * @return void
	 * @title: validateEmailCaptcha
	 * @Description //校验邮箱验证码
	 * @Param [code, email]
	 **/
	public void validateEmailCaptcha(String code, String email) {
		String verifyKey = CAPTCHA_CODE_KEY + email;
		String captcha = redisCache.getCacheObject(verifyKey);
		redisCache.deleteObject(verifyKey);
		if (captcha == null) {
			throw new ServerException("验证码已过期");
		}
		if (!code.equalsIgnoreCase(captcha)) {
			throw new ServerException("验证码填写错误");
		}
	}
}
