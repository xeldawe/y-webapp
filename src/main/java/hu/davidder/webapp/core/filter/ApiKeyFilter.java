package hu.davidder.webapp.core.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyFilter implements Filter {

	//https://www.keycloak.org/ should be better :)
	@Value("${API_KEY:abc123}")
	private String apiKey;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }
		String requestApiKey = httpRequest.getHeader("x-api-key");

		if (apiKey.equals(requestApiKey)) {
			chain.doFilter(request, response);
		} else {
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
		}
	}

}
