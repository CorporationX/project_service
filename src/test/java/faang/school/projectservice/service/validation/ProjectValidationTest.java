package faang.school.projectservice.service.validation;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ProjectValidationTest {
    @Spy
    private ProjectValidator projectValidator;

    @Test
    public void testToCreateUnnamed() {
        CreateSubProjectDto testValidatorSubProject = CreateSubProjectDto.builder()
                .visibility(PUBLIC)
                .description("test")
                .parentProjectId(1L)
                .build();
        assertThrows(NullPointerException.class, () -> projectValidator.toCreate(testValidatorSubProject));
    }

    @Test
    public void testToCreateNonDescription() {
        CreateSubProjectDto testValidatorSubProject = CreateSubProjectDto.builder()
                .name("test")
                .visibility(PUBLIC)
                .parentProjectId(1L)
                .build();
        assertThrows(NullPointerException.class, () -> projectValidator.toCreate(testValidatorSubProject));
    }

    @Test
    public void testToCreateParentIsNotFilled() {
        CreateSubProjectDto testValidatorSubProject = CreateSubProjectDto.builder()
                .visibility(PUBLIC)
                .name("test")
                .description("test")
                .build();
        assertThrows(NullPointerException.class, () -> projectValidator.toCreate(testValidatorSubProject));
    }

    @Test
    public void testToCreateVisibilityIsNotFilled() {
        CreateSubProjectDto testValidatorSubProject = CreateSubProjectDto.builder()
                .name("test")
                .description("test")
                .parentProjectId(1L)
                .build();
        assertThrows(NullPointerException.class, () -> projectValidator.toCreate(testValidatorSubProject));
    }

    @Test
    public void testCheckProjectDtoStatusIsNotFilled() {
        ProjectDto testValidatorProject = ProjectDto.builder()
                .name("test")
                .visibility(PUBLIC)
                .build();
        assertThrows(NullPointerException.class, () -> projectValidator.checkProjectDto(testValidatorProject));
    }
}
