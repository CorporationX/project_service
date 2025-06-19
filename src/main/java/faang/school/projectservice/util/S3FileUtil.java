package faang.school.projectservice.util;

import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.exception.common.FileCorruptedException;
import faang.school.projectservice.model.Project;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
@Component
public class S3FileUtil {

    private final S3Properties properties;

    public String getFolder(Project project) {
        return properties.getFilename().getFolderTemplate()
                .replace("{projectName}", project.getName())
                .replace("{projectId}", project.getId().toString());
    }

    public String getKey(String folder, MultipartFile file) {
        if(!Objects.nonNull(file.getContentType())) {
            throw new FileCorruptedException("File has no specific content type!");
        }
        return properties.getFilename().getKeyTemplate()
                .replace("{folder}", folder)
                .replace("{contentType}", file.getContentType())
                .replace("{fileName}", getSafeKey(file))
                .replace("{uploadTimeMillis}", String.valueOf(System.currentTimeMillis()));
    }

    public String getKey(Project project, MultipartFile file) {
        return getKey(getFolder(project), file);
    }

    public String getSafeKey(MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename())
                .replaceAll(properties.getFilename().getKeyWhitelist(), properties.getFilename().getReplacementChar());
    }

    public String getSafeMetadataName(MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename())
                .replaceAll(properties.getFilename().getMetadataWhitelist(), properties.getFilename().getReplacementChar());
    }
}