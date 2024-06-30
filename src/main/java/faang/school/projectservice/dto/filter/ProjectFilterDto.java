package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFilterDto {

    @Length(max = 128, message = "Name length should be less than 128")
    private String name;

    private ProjectStatus status;
}
