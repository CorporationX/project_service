package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.TeamRole;

import java.util.List;

public record ProjectTeamMemberDto(
        String name,
        List<TeamRole> roles) {

}
