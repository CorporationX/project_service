package faang.school.projectservice.dto.client.initiative;

import faang.school.projectservice.dto.client.project.ReadProjectDto;
import faang.school.projectservice.dto.client.teammember.ReadTeamMemberDto;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import lombok.Value;


@Value
public class ReadInitiativeDto {

    Integer id;
    ReadProjectDto project;
    ReadTeamMemberDto curator;
    InitiativeStatus status;
    String name;
    String description;
}
