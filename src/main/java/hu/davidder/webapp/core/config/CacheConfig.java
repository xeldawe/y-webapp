package hu.davidder.webapp.core.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import hu.davidder.webapp.core.base.order.entity.Order;
import hu.davidder.webapp.core.base.pet.entity.Pet;
import hu.davidder.webapp.core.util.RedisUtil;
import jakarta.annotation.PostConstruct;

@Configuration
@PropertySource("classpath:cache.properties")
@EnableCaching
public class CacheConfig {

	@Value("${orders.cache.duration}")
	private long ordersCacheDuration;

	@Value("${order.cache.duration}")
	private long orderCacheDuration;

	@Value("${pets.cache.duration}")
	private long petsCacheDuration;
	
	@Autowired
	private RedisUtil redisUtil;
	
	
    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);
    @PostConstruct
    public void logCacheConfig() {
        logger.info("Cache Configurations initialized with ordersCacheDuration={} and orderCacheDuration={}", ordersCacheDuration, orderCacheDuration);
    }

	/**
	 * Customizes the RedisCacheManager with specific cache configurations.
	 * 
	 * @return The RedisCacheManagerBuilderCustomizer bean.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Bean
	public RedisCacheManagerBuilderCustomizer customizer() {
		redisUtil.resetCache(); // Purge cache on startup
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.registerModule(new Jdk8Module());
		JavaType orderType = mapper.getTypeFactory().constructType(new TypeReference<Iterable<Order>>() {});
		JavaType petType = mapper.getTypeFactory().constructType(new TypeReference<Iterable<Pet>>() {});
		return builder -> builder
				.withCacheConfiguration("orders",
						RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(ordersCacheDuration))
								.serializeValuesWith(RedisSerializationContext.SerializationPair
										.fromSerializer(new Jackson2JsonRedisSerializer(mapper, orderType))))
				.withCacheConfiguration("order",
						RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(orderCacheDuration))
								.serializeValuesWith(RedisSerializationContext.SerializationPair
										.fromSerializer(new Jackson2JsonRedisSerializer(mapper, Order.class))))
				.withCacheConfiguration("pets",
						RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(petsCacheDuration))
								.serializeValuesWith(RedisSerializationContext.SerializationPair
										.fromSerializer(new Jackson2JsonRedisSerializer(mapper, petType))));
	}

}