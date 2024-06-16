package faang.school.projectservice.dto.project;

import java.math.BigInteger;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class ProjectDto {
    private Long id;

    @NotBlank(message = "Project name can't be blank")
    private String name;

    @NotBlank(message = "Project description can't be blank")
    private String description;

    @PositiveOrZero(message = "Storage size can't be negative")
    @Min(value = 1, message = "Storage size minimum value is 1")
    @NotNull(message = "Storage size can't be null")
    private BigInteger storageSize;

    @PositiveOrZero(message = "Storage max size can't be negative")
    @Min(value = 1, message = "Storage max size minimum value is 1")
    @NotNull(message = "Storage max size can't be null")
    private BigInteger maxStorageSize;

    @NotNull(message = "Project owner id")
    private Long ownerId;

    private Long parentProjectId;

    private List<@NotNull Long> children;

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