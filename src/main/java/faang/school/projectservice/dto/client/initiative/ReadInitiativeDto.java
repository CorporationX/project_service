package faang.school.projectservice.dto.client.initiative;

import faang.school.projectservice.model.Project;
import lombok.Value;


@Value
public class ReadInitiativeDto {

    Integer id;
    Project project;
    String name;
    String description;
}
