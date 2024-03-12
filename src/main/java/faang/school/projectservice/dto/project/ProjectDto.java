package faang.school.projectservice.dto.project;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectDto {

    private long id;
    private String name;
    private String description;
    private long ownerId;
    private String status;
    private String visibility;

}
