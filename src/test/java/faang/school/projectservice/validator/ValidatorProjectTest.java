package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidatorProjectTest {
    @InjectMocks
    private ValidatorProject validator;
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    @Test
    public void testValidationIsNullName() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        when(mapper.toEntity(projectDto)).thenReturn(projectEntity);

        assertThrows(NoSuchElementException.class, () -> validator.validationName(projectDto));
    }

    @Test
    public void testValidationNameIsBlank() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        projectEntity.setName("  ");
        when(mapper.toEntity(projectDto)).thenReturn(projectEntity);

        assertThrows(NoSuchElementException.class, () -> validator.validationName(projectDto));
    }

    @Test
    public void testValidationIsNullDescription() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        when(mapper.toEntity(projectDto)).thenReturn(projectEntity);

        assertThrows(NoSuchElementException.class, () -> validator.validationDescription(projectDto));
    }

    @Test
    public void testValidationDescriptionIsBlank() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        projectEntity.setDescription("  ");
        when(mapper.toEntity(projectDto)).thenReturn(projectEntity);

        assertThrows(NoSuchElementException.class, () -> validator.validationDescription(projectDto));
    }

    @Test
    public void testValidationExistingProject() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        Project existingProject = new Project();

        projectEntity.setId(1L);
        projectEntity.setOwnerId(1L);
        existingProject.setId(1L);
        existingProject.setName("Name");
        projectEntity.setName("Name");
        List<Project> projects = new ArrayList<>();

        projects.add(projectEntity);

        projectDto.setName("Name");

        when(mapper.toEntity(projectDto)).thenReturn(projectEntity);
        when(projectRepository.findAll()).thenReturn(projects);

        assertThrows(NoSuchElementException.class,
                () -> validator.validationDuplicateProjectNames(projectDto));
    }
}
