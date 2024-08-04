package faang.school.projectservice.filter.initiative;

import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import lombok.Data;

import java.util.List;

@Data
public class InitiativeFilterHelper {
    private InitiativeFilterDto initiativeFilterDto;
    private final Long curatorId = 1L;
    private final Long curatorIdNegative = 2L;
    private TeamMember teamMember;

    public Initiative initiativeFirst(){
        return Initiative
                .builder()
                .curator(teamMember())
                .status(InitiativeStatus.DONE)
                .build();
    }

    public Initiative initiativeSecond(){
        return Initiative
                .builder()
                .curator(teamMember())
                .status(InitiativeStatus.DONE)
                .build();
    }

    public TeamMember teamMember(){
        return TeamMember.builder()
                .id(curatorId)
                .build();
    }


    public InitiativeFilterDto initiativeFilterDto(){
        return InitiativeFilterDto.builder()
                .curatorPattern(curatorId)
                .statusPattern(InitiativeStatus.DONE)
                .build();
    }

    public List<Initiative> initiatives() {
        return List.of(initiativeFirst(), initiativeSecond());
    }
}
