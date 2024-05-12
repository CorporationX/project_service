package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectMapperTest {

    ProjectMapper projectMapper;
    Project project;
    ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        projectMapper = new ProjectMapperImpl();

        project = Project.builder()
                .name("Project1")
                .description("Description1")
                .ownerId(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.MAX)
                .updatedAt(LocalDateTime.MIN)
                .build();

        projectDto = ProjectDto.builder()
                .name("Project1")
                .description("Description1")
                .ownerId(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.MAX)
                .updatedAt(LocalDateTime.MIN)
                .build();
    }

    @Test
    public void testToDto() {
        ProjectDto resProjectDto = projectMapper.toDto(project);
        assertEquals(projectDto, resProjectDto);
    }

    @Test
    public void testToEntity() {
        Project resProject = projectMapper.toEntity(projectDto);
        assertEquals(project, resProject);
    }
}