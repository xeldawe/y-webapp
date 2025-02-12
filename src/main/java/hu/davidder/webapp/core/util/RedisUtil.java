package hu.davidder.webapp.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisUtil {
    @Lazy
	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate redisTemplate;
    
    public void resetCache() {
    	redisTemplate.getRequiredConnectionFactory().getConnection().commands().flushDb(); 
    }

	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
    
    
}
