package faang.school.projectservice.publisher;


import faang.school.projectservice.dto.sub_project.SubProjectCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static faang.school.projectservice.publisher.SubProjectCreatedEventPublisherTestData.TEST_TOPIC;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование SubProjectCreatedEventPublisher")
class SubProjectCreatedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RetryTemplate retryTemplate;

    @InjectMocks
    private SubProjectCreatedEventPublisher publisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "subProjectCreatedTopic", "test-topic");
    }

    @Test
    void shouldReturnCorrectTopic() {
        SubProjectCreatedEvent event = new SubProjectCreatedEvent(1L, 2L, 3L);
        publisher.publish(event);

        assertThat(publisher.getTopic()).isEqualTo(TEST_TOPIC);
    }



}