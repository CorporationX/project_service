package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import lombok.*;

import java.util.Objects;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFilterDto{
      private  String name;
      private  ProjectStatus projectStatus;
}
