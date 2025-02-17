package hu.davidder.webapp.core.filter;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import hu.davidder.webapp.core.security.ApiKeyAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyFilter extends GenericFilterBean {

//	// https://www.keycloak.org/ should be better :)
//	@Value("${API_KEY:abc123}")
//	private String apiKey;
//
//	// implements filter
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		HttpServletRequest httpRequest = (HttpServletRequest) request;
//		HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
//            httpResponse.setStatus(HttpServletResponse.SC_OK);
//            chain.doFilter(request, response);
//            return;
//        }
//		String requestApiKey = httpRequest.getHeader("x-api-key");
//
//		if (apiKey.equals(requestApiKey)) {
//			chain.doFilter(request, response);
//		} else {
//			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
//		}
//	}

	private final String apiKey;

	public ApiKeyFilter(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestApiKey = httpRequest.getHeader("X-API-KEY");
		if (requestApiKey != null) {
			if (apiKey.equals(requestApiKey)) {
				ApiKeyAuthenticationToken authentication = new ApiKeyAuthenticationToken("apiKeyUser", null);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
			}
		}
		chain.doFilter(request, response);
	}

}
