package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterSubProjectDto {
    @Size(max = 1000, message = "Project name Pattern must not exceed 1000 characters")
    private String namePattern;
    @Size(max = 128, message = "Project status Pattern must not exceed 128 characters")
    private String statusPattern;
}
