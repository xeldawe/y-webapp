package hu.davidder.webapp.conf;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@TestConfiguration
public class NoRedisTestConfig {

	@Bean
	@Primary
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory();
	}

	@Bean
	@Primary
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}
}
