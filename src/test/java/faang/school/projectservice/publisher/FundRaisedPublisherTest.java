package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.FundRaisedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FundRaisedPublisherTest {
    @InjectMocks
    private FundRaisedEventPublisher fundRaisedEventPublisher;

    @Mock
    private ChannelTopic donationTopic;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;
    private FundRaisedEvent fundRaisedEvent;

    @BeforeEach
    void setUp() {
        fundRaisedEvent = new FundRaisedEvent(12L, 34L, 5L, LocalDateTime.now());
    }

    @Test
    void testPublishEventToRedisSuccess() {
        when(donationTopic.getTopic()).thenReturn("donation-topic");

        fundRaisedEventPublisher.publish(fundRaisedEvent);

        ArgumentCaptor<FundRaisedEvent> argumentCaptor = ArgumentCaptor.forClass(FundRaisedEvent.class);
        verify(redisTemplate, times(1))
                .convertAndSend(eq("donation-topic"), argumentCaptor.capture());

        FundRaisedEvent actualEvent = argumentCaptor.getValue();

        assertEquals(fundRaisedEvent, actualEvent);
    }

//    @Test
//    void testPublishEventToRedisSuccess() throws JsonProcessingException {
//        String json = "{\"userId\":1,\"projectId\":100,\"paymentAmount\":500,\"localDateTime\":\"2024-12-12T10:00:00\"}";
//        when(objectMapper.writeValueAsString(fundRaisedEvent)).thenReturn(json);
//        when(donationTopic.getTopic()).thenReturn("donation-topic");
//
//        fundRaisedEventPublisher.publish(fundRaisedEvent);
//
//        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
//        verify(redisTemplate, times(1)).
//                convertAndSend(eq("donation-topic"), argumentCaptor.capture());
//
//        String actualJson = argumentCaptor.getValue();
//        assertEquals(json, actualJson);
//    }

    @Test
    void testPublishEventToRedisWithJSONProcessingException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(fundRaisedEvent)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> fundRaisedEventPublisher.publish(fundRaisedEvent));
    }
}
