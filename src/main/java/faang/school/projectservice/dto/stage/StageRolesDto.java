package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import lombok.EqualsAndHashCode;


public record StageRolesDto(@EqualsAndHashCode.Include TeamRole teamRole,
                            @EqualsAndHashCode.Exclude Integer count) {

}

