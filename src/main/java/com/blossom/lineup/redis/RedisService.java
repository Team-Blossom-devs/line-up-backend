package com.blossom.lineup.redis;

import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.exceptions.BusinessException;
import com.blossom.lineup.base.exceptions.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis 서비스
 * 유저 관련 key 값은 member:{데이터 의미}:{id} 값 형식으로 ex) member:qr:1
 */
@Slf4j
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
		redisTemplate.opsForValue().set(key, Base64.getEncoder().encodeToString(data));
	}

	/**
	 * byte 배열 데이터를 TTL과 함께 저장
	 * @param key : 키 값
	 * @param data : byte 배열 value
	 * @param duration : TTL 설정 시간
	 */
	public void setByteData(String key, byte[] data, Duration duration) {
		redisTemplate.opsForValue().set(key, Base64.getEncoder().encodeToString(data), duration);
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
		try{
			return Optional.ofNullable(
					Base64.getDecoder().decode((String) redisTemplate.opsForValue().get(key))
			);
		} catch (NullPointerException e) {
			// Redis에 키가 존재 하지 않음.
			log.error("[QR-code] QR CODE가 존재 하지 않습니다.");
			throw new BusinessException(Code.QRCODE_IS_NULL);
		} catch (IllegalArgumentException e){
			log.error("[QR-code] Base64 디코딩 중 문제가 발생했습니다.");
			throw new ServerException(Code.QRCODE_READING_ERROR);
		}
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
