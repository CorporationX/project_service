package faang.school.projectservice.service.s3;

import faang.school.projectservice.config.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class FileKeyGenerator {

    private static final String FILE_NAME_TIMESTAMP_PATTERN = "yyyyMMdd_HHmmss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FILE_NAME_TIMESTAMP_PATTERN);
    private static final String PROJECT_KEY_PREFIX = "project";
    private final S3Properties s3Properties;

    public String generateForProject(Long projectId) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        return String.format("%s/%s_%d_%s.pdf",
                s3Properties.presentationFolder(),
                PROJECT_KEY_PREFIX,
                projectId,
                timestamp
        );
    }
}
