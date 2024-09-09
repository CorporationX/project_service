package faang.school.projectservice.mapper.util;

import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("StageMapperUtil")
@Component
@RequiredArgsConstructor
public class StageMapperUtil {
    private final ProjectRepository projectRepository;
    private final StageRolesRepository stageRolesRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Named("mapRoles")
    private Map<Long, Integer> mapRoles(List<StageRoles> roles) {
        return roles.stream()
                .collect(Collectors.toMap(
                        StageRoles::getId,
                        StageRoles::getCount
                ));
    }

    @Named("toExecutorsIds")
    private List<Long> toExecutorsIds(List<TeamMember> members) {
        return members.stream()
                .map(TeamMember::getId)
                .toList();
    }

    @Named("getProjectById")
    public Project getProjectById(Long id) {
        return projectRepository.getProjectById(id);
    }

    @Named("getStageRolesById")
    public List<StageRoles> getStageRolesByIds(Map<Long, Integer> idMap) {
        return idMap.keySet().stream()
                .map(stageRolesRepository::getReferenceById)
                .toList();
    }

    @Named("getStageRolesById")
    public List<TeamMember> getExecutorsByIds(List<Long> ids) {
        return ids.stream()
                .map(teamMemberRepository::findById)
                .toList();
    }
}
