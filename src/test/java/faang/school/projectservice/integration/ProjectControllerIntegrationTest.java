package faang.school.projectservice.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import faang.school.projectservice.config.TestContainerConfig;
import faang.school.projectservice.dto.event.ProjectCreateEvent;
import faang.school.projectservice.dto.project.ProjectCreateRequestDto;
import faang.school.projectservice.dto.project.ProjectCreateResponseDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.JsonTestUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EmbeddedKafka(partitions = 1, topics = "project-create")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ProjectControllerIntegrationTest extends TestContainerConfig {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ProjectRepository projectRepository;

    private final BlockingQueue<ConsumerRecord<String, ProjectCreateEvent>> records = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "${spring.kafka.consumer.project-create.topic}",
            containerFactory = "kafkaProjectCreateListenerContFactory")
    public void listen(ConsumerRecord<String, ProjectCreateEvent> record) {
        records.add(record);
    }

    @Test
    void createProjectTest_Success() throws InterruptedException {
        ProjectCreateRequestDto input = JsonTestUtil
                .readJsonFromFile("json/project/create_project_request.json", new TypeReference<>() {});
        ProjectCreateResponseDto expectedSavedProject = JsonTestUtil
                .readJsonFromFile("json/project/create_project_response.json", new TypeReference<>() {});
        ProjectCreateEvent expectedProjectCreateEvent = new ProjectCreateEvent(input.getOwnerId(), 1);

        ProjectCreateResponseDto result = webTestClient.post()
                .uri("/projects")
                .bodyValue(input)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProjectCreateResponseDto.class)
                .returnResult()
                .getResponseBody();

        ConsumerRecord<String, ProjectCreateEvent> receivedEvent = records.poll(10, TimeUnit.SECONDS);
        expectedSavedProject.setCreatedAt(Objects.requireNonNull(result).getCreatedAt());
        Project projectInDatabase = projectRepository.findById(1L).orElse(new Project());

        assertThat(projectInDatabase)
                .matches(project -> expectedSavedProject.getName().equals(project.getName()));
        assertThat(receivedEvent)
                .isNotNull()
                .extracting(ConsumerRecord::value)
                .isEqualTo(expectedProjectCreateEvent);
        assertThat(result).isEqualTo(expectedSavedProject);

        projectRepository.deleteById(1L);
    }
}
