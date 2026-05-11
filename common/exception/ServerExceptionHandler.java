package myproject.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import lombok.extern.slf4j.Slf4j;
import myproject.common.utils.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;


/**
 * 异常处理器
 */
@Slf4j
@RestControllerAdvice
public class ServerExceptionHandler {
	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(ServerException.class)
	public Result<String> handleException(ServerException ex) {

		return Result.error(ex.getCode(), ex.getMsg());
	}

	/**
	 * SpringMVC参数绑定，Validator校验不正确
	 */
	@ExceptionHandler(BindException.class)
	public Result<String> bindException(BindException ex) {
		FieldError fieldError = ex.getFieldError();
		assert fieldError != null;
		return Result.error(fieldError.getDefaultMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public Result<String> handleAccessDeniedException(Exception ex) {

		return Result.error(ErrorCode.FORBIDDEN.getCode(), ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public Result<String> handleException(Exception ex) {
		log.error(ex.getMessage(), ex);
		return Result.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage());
	}

	// 全局异常拦截
	@ExceptionHandler(NotLoginException.class)
	public Result<String> handlerException(Exception e) {
		e.printStackTrace();
		return Result.error(e.getMessage());
	}
}