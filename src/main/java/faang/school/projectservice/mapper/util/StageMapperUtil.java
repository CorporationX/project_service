package faang.school.projectservice.mapper.util;

import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Named("StageMapperUtil")
@Component
@RequiredArgsConstructor
public class StageMapperUtil {
    private final ProjectRepository projectRepository;
    private final StageRolesRepository stageRolesRepository;
    private final TeamMemberRepository teamMemberRepository;


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
    public List<TeamMember> getExecutorsByIds(List<Long> ids) {
        return ids.stream()
                .map(teamMemberRepository::findById)
                .toList();
    }
}
