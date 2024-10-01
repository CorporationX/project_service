package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ValidatorProjectTest {
    @InjectMocks
    private ValidatorProject validator;
    @Spy
    private ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    @Test
    public void testValidationIsNullName() {
        ProjectDto projectDto = ProjectDto.builder().build();

        assertThrows(NoSuchElementException.class, () -> validator.validateProject(projectDto));
    }

    @Test
    public void testValidationNameIsBlank() {
        ProjectDto projectDto = ProjectDto.builder().build();
        Project projectEntity = new Project();
        projectEntity.setName("  ");

        assertThrows(NoSuchElementException.class, () -> validator.validateProject(projectDto));
    }

    @Test
    public void testValidationIsNullDescription() {
        ProjectDto projectDto = ProjectDto.builder().build();

        assertThrows(NoSuchElementException.class, () -> validator.validateProject(projectDto));
    }

    @Test
    public void testValidationDescriptionIsBlank() {
        ProjectDto projectDto = ProjectDto.builder().build();
        Project projectEntity = new Project();
        projectEntity.setDescription("  ");

        assertThrows(NoSuchElementException.class, () -> validator.validateProject(projectDto));
    }
}



