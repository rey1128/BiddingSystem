package com.rey.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.rey.service.EndpointService;

@Configuration
public class RedisConfig {
	@Value("${redis.port:6379}")
	private int redisPort;
	@Value("${redis.host:localhost}")
	private String redisHost;

	@Autowired
	private EndpointService endpointSrv;
	private Logger log = LoggerFactory.getLogger(RedisConfig.class);

	@Bean
	InitializingBean initEndpoints() {
		return () -> {
			try {
				endpointSrv.resetEndpoints();
				log.info("successfully reset endpoints in redis");
			} catch (Exception e) {
				log.error("error with reset endpoints in redis, error: " + e.getMessage());
			}
		};
	}

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
		conf.setHostName(redisHost);
		conf.setPort(redisPort);
		JedisConnectionFactory factory = new JedisConnectionFactory(conf);
		return factory;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		return template;
	};

}
