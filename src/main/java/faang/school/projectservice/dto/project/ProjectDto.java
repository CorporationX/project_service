package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    @NotNull(message = "Id should not be null")
    @Positive(message = "Id should be positive")
    private Long id;

    @NotNull(message = "Name should not be null")
    @NotBlank(message = "Name should not be blank")
    @Length(max = 128, message = "Name length should be less than 128")
    private String name;

    @Length(max = 4096, message = "Description length should be less than 4096")
    private String description;

    @Positive(message = "OwnerId should be positive")
    private Long ownerId;

    @NotNull(message = "Status should not be null")
    private ProjectStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
