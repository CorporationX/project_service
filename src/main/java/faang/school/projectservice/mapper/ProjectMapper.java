package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMapper {

    public static ProjectDto toDto(Project project) {
        if (project == null) return null;

        List<Long> participantsId = project.getTeams() == null ? List.of() :
                project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .map(TeamMember::getId)
                        .distinct()
                        .toList();

        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .ownerId(project.getOwnerId())
                .status(project.getStatus())
                .visibility(project.getVisibility())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    public static Project toEntity(ProjectCreateDto dto) {
        if (dto == null) return null;

        return Project.builder()
                .name(dto.name())
                .description(dto.description())
                .visibility(dto.visibility())
                .build();
    }

    public static Project toEntity(ProjectUpdateDto dto, Project existing) {
        if (dto == null || existing == null) return existing;

        existing.setDescription(dto.description());
        existing.setVisibility(dto.visibility());
        existing.setStatus(dto.status());
        return existing;
    }
}