package faang.school.projectservice.mapper.util;

import faang.school.projectservice.dto.TeamMemberDto;
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

    @Named("getProjectById")
    public Project getProjectById(Long id) {
        return projectRepository.getProjectById(id);
    }

    @Named("getExecutors")
    public List<TeamMember> getExecutors(List<TeamMemberDto> dtos) {
        return dtos.stream()
                .map(dto -> teamMemberRepository.findById(dto.id()))
                .toList();
    }
}
