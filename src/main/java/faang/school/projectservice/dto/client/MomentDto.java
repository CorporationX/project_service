package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Project;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class MomentDto {
    private Long id;
    private String name;
    private List<Project> projects;
}
