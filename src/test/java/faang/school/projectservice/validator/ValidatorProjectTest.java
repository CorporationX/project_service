package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.ProjectDto;
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

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidatorProjectTest {
    @InjectMocks
    private ValidatorProject validator;
    @Spy
    private ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    @Test
    public void testValidationIsNullName() {
        ProjectDto projectDto = new ProjectDto();

        assertThrows(NoSuchElementException.class, () -> validator.validationName(projectDto));
    }

    @Test
    public void testValidationNameIsBlank() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        projectEntity.setName("  ");

        assertThrows(NoSuchElementException.class, () -> validator.validationName(projectDto));
    }

    @Test
    public void testValidationIsNullDescription() {
        ProjectDto projectDto = new ProjectDto();

        assertThrows(NoSuchElementException.class, () -> validator.validationDescription(projectDto));
    }

    @Test
    public void testValidationDescriptionIsBlank() {
        ProjectDto projectDto = new ProjectDto();
        Project projectEntity = new Project();
        projectEntity.setDescription("  ");

        assertThrows(NoSuchElementException.class, () -> validator.validationDescription(projectDto));
    }
}



