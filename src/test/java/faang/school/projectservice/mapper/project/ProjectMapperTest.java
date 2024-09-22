package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectMapperTest {

    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    private static final long ID = 1L;

    @Test
    @DisplayName("Успех маппинга project в projectDto")
    public void whenToDtoThenSuccess() {
        Project project = new Project();
        project.setId(ID);
        project.setName("name");
        project.setDescription("description");
        project.setOwnerId(ID);
        project.setCreatedAt(LocalDateTime.of(2020, 10, 10, 10, 10));
        project.setUpdatedAt(LocalDateTime.of(2021, 10, 10, 10, 10));
        project.setStatus(ProjectStatus.CREATED);

        ProjectDto projectDto = projectMapper.toDto(project);

        assertEquals(project.getId(), projectDto.getId());
        assertEquals(project.getName(), projectDto.getName());
        assertEquals(project.getDescription(), projectDto.getDescription());
        assertEquals(project.getOwnerId(), projectDto.getOwnerId());
        assertEquals(project.getCreatedAt(), projectDto.getCreatedAt());
        assertEquals(project.getUpdatedAt(), projectDto.getUpdatedAt());
        assertEquals(project.getStatus(), projectDto.getStatus());
    }

    @Test
    @DisplayName("Успех маппинга projectDto в project")
    public void whenToEntityThenSuccess() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(ID);
        projectDto.setName("name");
        projectDto.setDescription("description");
        projectDto.setOwnerId(ID);
        projectDto.setCreatedAt(LocalDateTime.of(2020, 10, 10, 10, 10));
        projectDto.setUpdatedAt(LocalDateTime.of(2021, 10, 10, 10, 10));
        projectDto.setStatus(ProjectStatus.CREATED);

        Project project = projectMapper.toEntity(projectDto);

        assertEquals(projectDto.getId(), project.getId());
        assertEquals(projectDto.getName(), project.getName());
        assertEquals(projectDto.getDescription(), project.getDescription());
        assertEquals(projectDto.getOwnerId(), project.getOwnerId());
        assertEquals(projectDto.getCreatedAt(), project.getCreatedAt());
        assertEquals(projectDto.getUpdatedAt(), project.getUpdatedAt());
        assertEquals(projectDto.getStatus(), project.getStatus());
    }

    @Test
    @DisplayName("Успех маппинга списка projects в список projectDtos")
    public void whenToDtosThenSuccess() {
        Project first = new Project();
        first.setId(ID);
        Project second = new Project();
        second.setId(ID);
        List<Project> projects = List.of(first, second);

        List<ProjectDto> projectDtos = projectMapper.toDtos(projects);

        assertEquals(2, projectDtos.size());
        assertEquals(first.getId(), projectDtos.get(0).getId());
        assertEquals(second.getId(), projectDtos.get(1).getId());
    }
}