package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

public record ProjectCoverUploadRequest(
        @NotNull(message = "Project ID must not be null")
        @Positive(message = "Project ID must be positive")
        Long projectId,

        @NotNull(message = "File must not be null")
        MultipartFile file
) {}