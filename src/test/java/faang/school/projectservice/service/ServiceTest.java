package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapper mapper;

    @Test
    public void testValidationIsNullName() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        Mockito.when(mapper.toEntity(projectDto)).thenReturn(projectEntity);

        Assertions.assertThrows(NoSuchElementException.class, () -> projectService.validationName(projectDto));
    }

    @Test
    public void testValidationNameIsBlank() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        projectEntity.setName("  ");
        Mockito.when(mapper.toEntity(projectDto)).thenReturn(projectEntity);

        Assertions.assertThrows(NoSuchElementException.class, () -> projectService.validationName(projectDto));
    }

    @Test
    public void testValidationIsNullDescription() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        Mockito.when(mapper.toEntity(projectDto)).thenReturn(projectEntity);

        Assertions.assertThrows(NoSuchElementException.class, () -> projectService.validationDescription(projectDto));
    }

    @Test
    public void testValidationDescriptionIsBlank() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        projectEntity.setDescription("  ");
        Mockito.when(mapper.toEntity(projectDto)).thenReturn(projectEntity);

        Assertions.assertThrows(NoSuchElementException.class, () -> projectService.validationDescription(projectDto));
    }

    @Test
    public void testValidationExistingProject() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        Project existingProject = new Project();

        projectEntity.setId(2L);
        projectEntity.setOwnerId(1l);
        existingProject.setId(1L);

        projectDto.setName("Second test name");

        Mockito.when(mapper.toEntity(projectDto)).thenReturn(projectEntity);
        Mockito.when(projectRepository.findProjectByNameAndOwnerId(projectDto.getName(), projectEntity.getOwnerId()))
                .thenReturn(existingProject);

        Assertions.assertThrows(NoSuchElementException.class,
                () -> projectService.validationDuplicateProjectNames(projectDto));
    }
}
