package faang.school.projectservice.dto;


import liquibase.hub.model.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MomentDto {

    private Long id;
    private String name;
    private LocalDateTime date;
    List<Project> partners;
}
