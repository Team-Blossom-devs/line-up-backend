package com.blossom.lineup.redis;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Redis 서비스
 * 유저 관련 key 값은 member:{데이터 의미}:{id} 값 형식으로 ex) member:qr:1
 */
@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * String 데이터를 저장
	 * @param key : 키 값
	 * @param data : 문자열 value
	 */
	public void setStringData(String key, String data) {
		redisTemplate.opsForValue().set(key, data);
	}

	/**
	 * String 데이터를 TTL과 함께 저장
	 * @param key : 키 값
	 * @param data : 문자열 value
	 * @param duration : TTL 설정 시간
	 */
	public void setStringData(String key, String data, Duration duration) {
		redisTemplate.opsForValue().set(key, data, duration);
	}

	/**
	 * byte 배열 데이터를 저장
	 * @param key : 키 값
	 * @param data : byte 배열 value
	 */
	public void setByteData(String key, byte[] data) {
		redisTemplate.opsForValue().set(key, data);
	}

	/**
	 * byte 배열 데이터를 TTL과 함께 저장
	 * @param key : 키 값
	 * @param data : byte 배열 value
	 * @param duration : TTL 설정 시간
	 */
	public void setByteData(String key, byte[] data, Duration duration) {
		redisTemplate.opsForValue().set(key, data, duration);
	}

	/**
	 * Optional로 String 값 찾아오기 -> 없으면 empty(null)
	 * @param key : 찾아오려는 데이터의 key
	 * @return : Nullable 한 String 데이터를 Optional 로 반환
	 */
	public Optional<String> getStringData(String key) {
		return Optional.ofNullable((String)redisTemplate.opsForValue().get(key));
	}

	/**
	 * Optional로 byte 배열 값 찾아오기 -> 없으면 empty(null)
	 * @param key : 찾아오려는 데이터의 key
	 * @return : Nullable 한 byte 배열 데이터를 Optional 로 반환
	 */
	public Optional<byte[]> getByteData(String key) {
		return Optional.ofNullable((byte[]) redisTemplate.opsForValue().get(key));
	}

	/**
	 * key 해당하는 데이터 삭제
	 * @param key : 삭제하려는 key
	 */
	public void deleteData(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * 데이터 TTL 설정
	 * @param key : 설정할 데이터의 Key
	 * @param timeout : 설정할 TTL (ms)
	 */
	public void expireData(String key, int timeout) {
		redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
	}

}