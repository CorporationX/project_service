package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class ResourceDto {
    private Long id;

    @NotBlank(message = "Resource name cannot be blank")
    @Size(max = 255, message = "Resource name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Resource key cannot be blank")
    @Size(max = 255, message = "Resource key cannot exceed 255 characters")
    private String key;

    @NotNull(message = "Resource type cannot be null")
    private ResourceType type;

    @NotNull(message = "Creation date cannot be null")
    private LocalDateTime createdAt;

    @NotNull(message = "Resource size cannot be null")
    private BigInteger size;

    @NotNull(message = "Update date cannot be null")
    private LocalDateTime updatedAt;

    @NotNull(message = "Project ID cannot be null")
    private Long projectId;
}