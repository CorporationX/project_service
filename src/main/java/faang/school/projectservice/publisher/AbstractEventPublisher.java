package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public abstract class AbstractEventPublisher <T> {
    private ObjectMapper objectMapper;
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    protected void publish(T event, String channel){
        try{
            String jsonEvent = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channel,jsonEvent);
        }catch (JsonProcessingException e){
            log.error("JsonProcessingException "+e);
        }
    }
}
