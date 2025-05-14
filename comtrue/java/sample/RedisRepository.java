package sample.api;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
redis 연동 샘플코드
*/
@Slf4j
@Service
public class RedisRepository {

    private final StringRedisTemplate stringRedisTemplate;

    private static StringRedisTemplate stringRedisTemplate2;
    
    public RedisRepository(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
        this.stringStringListOperations = stringRedisTemplate.opsForList();
        this.stringStringZSetOperations = stringRedisTemplate.opsForZSet();
        this.stringStringSetOperations = stringRedisTemplate.opsForSet();
        this.stringRedisTemplate2 = stringRedisTemplate;
    }

    private final HashOperations<String, Object, Object> stringObjectObjectHashOperations;
    private final ListOperations<String, String> stringStringListOperations;
    private final ZSetOperations<String, String> stringStringZSetOperations;
    private final SetOperations<String, String> stringStringSetOperations;

    public static String getPrivateDBName(String domain) throws Exception {
    	try {
            String value = stringRedisTemplate2.opsForValue().get("db/"+domain);
            return value;
        } catch (Exception e) {
            throw e;
        }
    }
    
    public void set(String key, String value, long expire) throws Exception {
        try {
            stringRedisTemplate.opsForValue().set(key, value, expire, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw e;
        }    }
    
    public void set(String key, String value) throws Exception {
    	try {
    		stringRedisTemplate.opsForValue().set(key, value);
    	} catch (Exception e) {
    		throw e;
    	}
    }

    public String get(String key) throws Exception {
        try {
            String value = stringRedisTemplate.opsForValue().get(key);
            return value;
        } catch (Exception e) {
            throw e;
        }
    }

    public Boolean expireAt(String key, Date date) throws Exception {
        try {
            return stringRedisTemplate.expireAt(key, date);
        } catch (Exception e) {
            throw e;
        }
    }

    public long deleteByKey(String key) throws Exception {
        try {
            if (stringRedisTemplate.delete(key)) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public long deleteByKeys(Collection<String> keys) throws Exception {
        try {
            return stringRedisTemplate.delete(keys);
        } catch (Exception e) {
            throw e;
        }
    }

    public long delete(Collection<String> keys) throws Exception {
        try {
            return stringRedisTemplate.delete(keys);
        } catch (Exception e) {
            throw e;
        }
    }

    public Set<String> getPatternKey(String pattern) throws Exception {
        try {
            return stringRedisTemplate.keys(pattern);
        } catch (Exception e) {
            throw e;
        }
    }

    public void renameKey(String oldKey, String newKey) throws Exception {
        try {
            stringRedisTemplate.rename(oldKey, newKey);
        } catch (Exception e) {
            throw e;
        }
    }

    public DataType getKeyType(String key) throws Exception {
        try {
            return stringRedisTemplate.type(key);
        } catch (Exception e) {
            throw e;
        }

    }

    public boolean setFromMap(Map<String, String> map) throws Exception {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().multiSetIfAbsent(map));
        } catch (Exception e) {
            throw e;
        }
    }

    public Map<Object, Object> hGetAll(String key) throws Exception {
        try {
            return stringObjectObjectHashOperations.entries(key);
        } catch (Exception e) {
            throw e;
        }
    }

    public void hPutAll(String key, Map<String, String> maps) throws Exception {
        try {
            stringObjectObjectHashOperations.putAll(key, maps);
        } catch (Exception e) {
            throw e;
        }
    }

    public void hPut(String key, String hashKey, String value) throws Exception {
        try {
            stringObjectObjectHashOperations.put(key, hashKey, value);
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean hashExists(String key, String field) throws Exception {
        try {
            return stringObjectObjectHashOperations.hasKey(key, field);
        } catch (Exception e) {
            throw e;
        }

    }

    public Set<Object> getHashFields(String key) throws Exception {
        try {
            return stringObjectObjectHashOperations.keys(key);
        } catch (Exception e) {
            throw e;
        }

    }

    public List<Object> hValues(String key) throws Exception {
        try {
            return stringObjectObjectHashOperations.values(key);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public void delInbound(String domain, int port) throws Exception {
    	log.debug("delete policy/inbound/"+domain +"\t, dupl/inbound/"+domain);
    	deleteByKey("policy/inbound/"+domain);
    	deleteByKey("dupl/inbound/"+domain);
    	if(port != -1) {
    		log.debug("delete policy/inbound/"+domain+":"+port +"\t, dupl/inbound/"+domain+":"+port);
    		deleteByKey("policy/inbound/"+domain+":"+port);
    		deleteByKey("dupl/inbound/"+domain+":"+port);
    	}
    	
    }
    public void delOutbound(String domain, int port) throws Exception {
    	log.debug("delete policy/outbound/"+domain +"\t, dupl/outbound/"+domain);
    	deleteByKey("policy/outbound/"+domain);
    	if(port != -1) {
    		log.debug("delete policy/outbound/"+domain+":"+port +"\t, dupl/outbound/"+domain+":"+port);
    		deleteByKey("policy/outbound/"+domain+":"+port);
    	}
    }
    
    public void setInbound(String domain) throws Exception {
    	log.debug("set policy/inbound/"+domain);
    	set("policy/inbound/"+domain, domain);
    }
    public void setOutbound(String domain) throws Exception {
    	log.debug("set policy/outbound/"+domain);
    	set("policy/outbound/"+domain, domain);
    }
    
    // hjkim [2022.10.14] 필터링웹서버 추가/수정 시 "db/domain:port" 를 set/del 하도록 추가 
    public void setFilteringServerDomain(String domain, Integer port, Integer id) throws Exception {
    	log.debug("set db/"+domain);
    	set(String.valueOf("db/"+domain), String.valueOf("privacycenter_"+id));
    	if(port!= -1) {
    		log.debug("set db/"+domain+":"+port);
    		set(String.valueOf("db/"+domain+":"+port), String.valueOf("privacycenter_"+id));    	
    	}
//    	set(String.valueOf("db/"+domain), "privacycenter_master");
//    	set(String.valueOf("db/"+domain+":"+port), "privacycenter_master");
    }
    
    public void delFilteringServerDomain(String domain, Integer port, boolean deleteCheck) throws Exception {
    	if(deleteCheck) {
    		log.debug("delete db/"+domain);
    		deleteByKey("db/"+domain);
    	}
    	if(port!= -1) {
    		log.debug("delete db/"+domain+":"+port);
    		deleteByKey("db/"+domain+":"+port);
    	}
    }
    
}
