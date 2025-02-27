package faang.school.projectservice.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import faang.school.projectservice.client.feign.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import feign.FeignException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import freemarker.template.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PdfReportGeneratorService {
    private static final Logger log = LoggerFactory.getLogger(PdfReportGeneratorService.class);
    private final Configuration freemarkerConfig;
    private final UserServiceClient userServiceClient;

    public Resource generateProjectReport(Project project) {
        String htmlContent = generateHtmlReport(project);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            renderPdf(htmlContent, outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error generating project report", e);
        }
    }

    private String generateHtmlReport(Project project) {
        Map<String, Object> model = new HashMap<>();
        model.put("project", project);
        model.put("ownerName", getOwnerName(project.getOwnerId()));
        model.put("completedTasks", project.getTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .collect(Collectors.toList()));
        model.put("totalTeamMembers", project.getTeams().stream()
                .mapToInt(team -> team.getTeamMembers().size()).sum());

        try {
            Template template = freemarkerConfig.getTemplate("project-report.ftl");
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("Error while processing Freemarker", e);
        }
    }

    private static void renderPdf(String html, OutputStream outputStream) throws Exception {
        try (outputStream) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
        }
    }

    private String getOwnerName(Long ownerId) {
        log.info("Get owner name: {}", ownerId);
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID cannot be null");
        }
        try {
            return Optional.ofNullable(userServiceClient.getUser(ownerId))
                    .map(response -> response.getBody().getUsername())
                    .orElse("Unknown User");
        } catch (FeignException.NotFound e) {
            log.warn("User with ID {} not found", ownerId);
            return "Unknown User";
        } catch (Exception e) {
            log.error("Error getting owner name for ID {}: {}", ownerId, e.getMessage(), e);
            throw new RuntimeException("Error getting owner name: " + ownerId, e);
        }
    }
}
