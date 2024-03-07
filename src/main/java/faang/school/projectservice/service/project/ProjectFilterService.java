package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectFilterService {

    private final List<ProjectFilter> projectFilters;
    private final UserContext userContext;
    private final TeamMemberJpaRepository teamMemberJpaRepository;

    public Stream<ProjectDto> applyFilters(Stream<ProjectDto> projectDtoStream, ProjectFilterDto filterDto) {
        projectDtoStream = filterVisibilityScope(projectDtoStream);
        for (ProjectFilter projectFilter : projectFilters) {
            if (projectFilter.isApplicable(filterDto)) {
                projectDtoStream = projectFilter.apply(projectDtoStream, filterDto);
            }
        }
        return projectDtoStream;
    }

    private Stream<ProjectDto> filterVisibilityScope(Stream<ProjectDto> projectDtoStream) {
        return projectDtoStream.filter(projectDto -> {
            if (projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
                long clientUserId = userContext.getUserId();
                TeamMember teamMember = teamMemberJpaRepository.findByUserIdAndProjectId(clientUserId, projectDto.getId());
                return teamMember != null;
            }
            return true;
        });
    }

}