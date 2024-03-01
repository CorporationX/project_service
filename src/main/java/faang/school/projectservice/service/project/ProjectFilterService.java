package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Project;
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

    public Stream<Project> applyFilters(Stream<Project> projectStream, ProjectFilterDto filterDto) {
        projectStream = filterVisibilityScope(projectStream);
        if (filterDto != null) {
            for (ProjectFilter projectFilter : projectFilters) {
                if (projectFilter.isApplicable(filterDto)) {
                    projectStream = projectFilter.apply(projectStream, filterDto);
                }
            }
        }
        return projectStream;
    }

    private Stream<Project> filterVisibilityScope(Stream<Project> projectStream) {
        return projectStream.filter(project -> {
            if (project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
                long clientUserId = userContext.getUserId();
                TeamMember teamMember = teamMemberJpaRepository.findByUserIdAndProjectId(clientUserId, project.getId());
                if (teamMember == null) {
                    return false;
                }
            }
            return true;
        });
    }

}