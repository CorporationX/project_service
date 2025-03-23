package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FilterSubProjectDto {
        @NotBlank(message = "Name must not be blank") private String name;
        private ProjectStatus status;
}
