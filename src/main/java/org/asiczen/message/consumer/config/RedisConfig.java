package org.asiczen.message.consumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

//	@Bean
//	public JedisConnectionFactory redisConnectionFactory() {
//		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
////		config.setHostName("localhost");
////		config.setPort(6379);
//		return new JedisConnectionFactory(config);
//	}

	@Bean
	public JedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("us1-flying-glowworm-30858.lambda.store",30858);
		config.setPassword(RedisPassword.of("75d72024332740a2bcda32dc846d2a28"));
		return new JedisConnectionFactory(config);
	}

	@Bean
	ChannelTopic topic() {
		return new ChannelTopic("messagesource");
	}

	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();

		poolConfig.setMaxIdle(0);
		poolConfig.setMaxTotal(3);
		poolConfig.setTestOnBorrow(false);
		poolConfig.setTestOnReturn(false);
		poolConfig.setTestOnCreate(false);
		poolConfig.setTestWhileIdle(false);
		poolConfig.setMinEvictableIdleTimeMillis(6);
		poolConfig.setTimeBetweenEvictionRunsMillis(3);
		poolConfig.setNumTestsPerEvictionRun(-1);
		poolConfig.setFairness(true);
		return poolConfig;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());

		GenericJackson2JsonRedisSerializer valSerializer = new GenericJackson2JsonRedisSerializer();
		template.setValueSerializer(valSerializer);

		template.setKeySerializer(new RedisSerializer<Object>() {

			@Override
			public byte[] serialize(Object t) throws SerializationException {
				return (t == null ? null : (":" + t.toString()).getBytes());
			}

			@Override
			public Object deserialize(byte[] bytes) throws SerializationException {
				return (bytes == null ? null : new String(bytes));
			}
		});
		template.setHashValueSerializer(valSerializer);

		return template;
	}
}
