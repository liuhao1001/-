package myproject.common.utils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import java.rmi.ServerException;

/**
 * 校验工具类
 */
public class AssertUtils {

	public static void isBlank(String str, String variable) throws ServerException {
		if (StrUtil.isBlank(str)) {
			throw new ServerException(variable + "不能为空");
		}
	}

	public static void isNull(Object object, String variable) throws ServerException {
		if (object == null) {
			throw new ServerException(variable + "不能为空");
		}
	}

	public static void isArrayEmpty(Object[] array, String variable) throws ServerException {
		if (ArrayUtil.isEmpty(array)) {
			throw new ServerException(variable + "不能为空");
		}
	}

}