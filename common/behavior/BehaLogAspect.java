package myproject.common.behavior;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import myproject.common.utils.GloabUtils;
import myproject.common.utils.HttpContextUtils;
import myproject.service.StoreupService;
import myproject.vo.StoreupVO;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 操作行为日志
 */
@Service
@AllArgsConstructor
public class BehaLogAspect implements HandlerInterceptor {
	private final StoreupService storeupService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		HandlerMethod method = (HandlerMethod) handler;
		//判断是否有添加BehaLog注解
		BehaLog methodAnnotation = method.getMethodAnnotation(BehaLog.class);
		if (methodAnnotation == null) {
			//没有注解或注解直接放行
			return true;
		}
		Map<String, String> parameterMap = HttpContextUtils.getParameterMap(request);
		String id = parameterMap.get("id");
		if (StrUtil.isNotBlank(id) || GloabUtils.getUserId().equals(1L)) {
			Long value = Long.parseLong(id);
			StoreupVO storeupVO = new StoreupVO();
			storeupVO.setUserid(GloabUtils.getUserId());
			storeupVO.setRefid(value);
			storeupVO.setType(1);
			storeupService.save(storeupVO);
		}

		return true;
	}
}
