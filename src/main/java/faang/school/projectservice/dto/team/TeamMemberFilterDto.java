package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import lombok.Builder;

import java.util.List;

@Builder
public record TeamMemberFilterDto(String name,
                                  List<TeamRole> roles) {
}
