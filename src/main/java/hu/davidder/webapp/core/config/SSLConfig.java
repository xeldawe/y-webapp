package hu.davidder.webapp.core.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SSLConfig {

	@Value("${security.oauth2.resourceserver.jwt.jwk-set-uri}")
	private String jwkSetUri;

	@Bean
	public SSLContext sslContext() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		return SSLContextBuilder.create().loadTrustMaterial(null, acceptingTrustStrategy).build();
	}

	@Bean
	public CloseableHttpClient httpClient(SSLContext sslContext) {
		DefaultClientTlsStrategy tlsStrategy = new DefaultClientTlsStrategy(sslContext);
		PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
				.setTlsSocketStrategy(tlsStrategy).build();
		return HttpClients.custom().setConnectionManager(connectionManager).setConnectionManagerShared(true).build();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder, CloseableHttpClient httpClient) {
		return builder.requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient)).build();
	}
}
