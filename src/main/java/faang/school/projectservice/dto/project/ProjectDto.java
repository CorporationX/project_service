package faang.school.projectservice.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class ProjectDto {
    private Long id;

    @NotBlank(message = "Project name can't be blank")
    private String name;

    @NotBlank(message = "Project description can't be blank")
    private String description;

    @Positive(message = "Storage size can't be negative")
    @Min(value = 1, message = "Storage size can't be less than 1")
    @Max(value = 1000, message = "Storage size can't be greater than 1000")
    private BigInteger storageSize;

    @Positive(message = "Storage max size can't be negative")
    @Min(value = 1, message = "Storage max size can't be less than 1")
    @Max(value = 1000, message = "Storage max size can't be greater than 1000")
    private BigInteger maxStorageSize;

    @NotNull(message = "Project owner id")
    private Long ownerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private ProjectStatus status;

    @NotNull(message = "Project visibility can't be empty")
    private ProjectVisibility visibility;

    @JsonIgnore
    public boolean isStatusFinished() {
        return this.status == ProjectStatus.CANCELLED || this.status == ProjectStatus.COMPLETED;
    }

    @JsonIgnore
    public boolean isStorageSizeGreaterThanMaxStorageSize() {
        return storageSize.compareTo(maxStorageSize) > 0;
    }
}