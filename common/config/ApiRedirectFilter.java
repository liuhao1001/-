package myproject.common.config;


import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class ApiRedirectFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		String requestURI = httpRequest.getRequestURI();
		if (requestURI.startsWith("/api")) {
			String newURI = requestURI.replaceFirst("/api", "");
			// 使用 RequestDispatcher 进行内部转发
			request.getRequestDispatcher(newURI).forward(request, response);
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}


}
