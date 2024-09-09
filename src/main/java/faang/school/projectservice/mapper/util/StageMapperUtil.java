package faang.school.projectservice.mapper.util;

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
    private final TeamMemberRepository teamMemberRepository;

    @Named("toExecutorsIds")
    public List<Long> toExecutorsIds(List<TeamMember> executors){
        return executors.stream()
                .map(TeamMember::getId)
                .toList();
    }

    @Named("getProjectById")
    public Project getProjectById(Long id) {
        return projectRepository.getProjectById(id);
    }

    @Named("getExecutorsByIds")
    public List<TeamMember> getExecutorsByIds(List<Long> ids) {
        return ids.stream()
                .map(teamMemberRepository::findById)
                .toList();
    }
}
