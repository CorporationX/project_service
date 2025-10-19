package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMapper {

    public ProjectDto toDto(Project project) {
        if (project == null) return null;

        List<Long> participantsId = project.getTeams() == null ? List.of() :
                project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .map(TeamMember::getId)
                        .distinct()
                        .toList();

        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwnerId(),
                project.getStatus(),
                project.getVisibility(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    public Project toEntity(ProjectDto dto) {
        if (dto == null) return null;

        return Project.builder()
                .id(dto.id())
                .name(dto.name())
                .description(dto.description())
                .ownerId(dto.ownerId())
                .status(dto.status())
                .visibility(dto.visibility())
                .createdAt(dto.createdAt())
                .updatedAt(dto.updatedAt())
                .build();
    }
}
