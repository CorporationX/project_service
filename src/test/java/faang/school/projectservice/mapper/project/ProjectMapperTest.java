package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectMapperTest {
    @InjectMocks
    private ProjectMapperImpl mapper;

    @Test
    public void testToDto() {
        Project project = Project.builder()
                .name("name")
                .id(1L)
                .ownerId(1L)
                .description("des")
                .status(ProjectStatus.CREATED)
                .build();

        ProjectDto projectDto = mapper.toDto(project);

        equals(project, projectDto);
    }

    @Test
    public void testToEntity() {
        ProjectDto projectDto = ProjectDto.builder()
                .name("name")
                .id(1L)
                .ownerId(1L)
                .description("des")
                .status(ProjectStatus.CREATED)
                .build();

        Project project = mapper.toEntity(projectDto);

        equals(project, projectDto);
    }

    private void equals(Project project, ProjectDto projectDto) {
        Assertions.assertEquals(project.getId(), projectDto.getId());
        Assertions.assertEquals(project.getName(), projectDto.getName());
        Assertions.assertEquals(project.getDescription(), projectDto.getDescription());
        Assertions.assertEquals(project.getStatus(), projectDto.getStatus());
        Assertions.assertEquals(project.getOwnerId(), projectDto.getOwnerId());
    }
}
