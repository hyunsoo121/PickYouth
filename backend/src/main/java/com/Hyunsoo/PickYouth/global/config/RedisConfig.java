package com.Hyunsoo.PickYouth.global.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

  /**
   * {@link GenericJackson2JsonRedisSerializer}의 기본 생성자가 내부적으로 구성하는 ObjectMapper를 그대로 재현하되 {@code
   * JavaTimeModule}만 추가한다. 커스텀 ObjectMapper를 생성자에 넘기면 기본 생성자가 해주는 {@code activateDefaultTyping} 설정이
   * 함께 빠지는데, 이게 없으면 저장 시 타입 정보가 안 남아 캐시에서 읽을 때 {@code LinkedHashMap}으로 돌아와 {@code
   * ClassCastException}이 난다 (실측 확인).
   *
   * <p>{@code DefaultTyping.NON_FINAL}(Spring 기본값)이 아니라 {@code EVERYTHING}을 쓴다 — 우리가 캐싱하는 값은 {@code
   * SubsidyPageResponse} 같은 record(=final)라서, "정적 타입과 런타임 타입이 같고 final이면 타입 생략"하는 NON_FINAL 규칙에 걸려
   * 루트 객체 자체엔 {@code @class}가 하나도 안 붙는다 (실측 확인 — 캐시 저장은 성공하지만 재조회 시 "missing type id property
   * '@class'"로 깨짐). EVERYTHING은 이 예외 없이 항상 타입을 남긴다.
   */
  private ObjectMapper redisObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.EVERYTHING,
        JsonTypeInfo.As.PROPERTY);
    return mapper;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    GenericJackson2JsonRedisSerializer serializer =
        new GenericJackson2JsonRedisSerializer(redisObjectMapper());
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(serializer);
    return template;
  }

  /** 매칭 결과 캐싱용. 배치가 하루 1회(04:00) 갱신되므로 짧은 TTL로 최신성/부하 절감을 함께 챙긴다. */
  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration config =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer(redisObjectMapper())));
    return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
  }
}
