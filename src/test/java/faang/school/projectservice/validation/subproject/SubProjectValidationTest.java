package faang.school.projectservice.validation.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.handler.exceptions.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubProjectValidationTest {
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private SubProjectValidation subProjectValidation;

    @Test
    public void testToCreateUnnamed() {
        CreateSubProjectDto testValidatorSubProject = CreateSubProjectDto.builder()
                .visibility(PUBLIC)
                .description("test")
                .parentProjectId(1L)
                .build();
        assertThrows(NullPointerException.class, () -> subProjectValidation.toCreate(testValidatorSubProject));
    }

    @Test
    public void testToCreateNonDescription() {
        CreateSubProjectDto testValidatorSubProject = CreateSubProjectDto.builder()
                .name("test")
                .visibility(PUBLIC)
                .parentProjectId(1L)
                .build();
        assertThrows(NullPointerException.class, () -> subProjectValidation.toCreate(testValidatorSubProject));
    }

    @Test
    public void testToCreateParentIsNotFilled() {
        CreateSubProjectDto testValidatorSubProject = CreateSubProjectDto.builder()
                .visibility(PUBLIC)
                .name("test")
                .description("test")
                .build();
        assertThrows(NullPointerException.class, () -> subProjectValidation.toCreate(testValidatorSubProject));
    }

    @Test
    public void testToCreateVisibilityIsNotFilled() {
        CreateSubProjectDto testValidatorSubProject = CreateSubProjectDto.builder()
                .name("test")
                .description("test")
                .parentProjectId(1L)
                .build();
        assertThrows(NullPointerException.class, () -> subProjectValidation.toCreate(testValidatorSubProject));
    }

    @Test
    public void testCheckProjectDtoStatusIsNotFilled() {
        Project rootProject = Project.builder()
                .id(1L)
                .build();
        when(projectRepository.existsById(2L)).thenReturn(true);
        SubProjectDto testValidatorProject = SubProjectDto.builder()
                .id(2L)
                .name("test")
                .visibility(PUBLIC).parentProject(rootProject)
                .build();
        assertThrows(NullPointerException.class, () -> subProjectValidation.checkProjectDto(testValidatorProject));
    }
    @Test
    public void testCheckProjectDtoNonExistById() {
        SubProjectDto testValidatorProject = SubProjectDto.builder()
                .id(2L)
                .build();
        assertThrows(EntityNotFoundException.class, () -> subProjectValidation.checkProjectDto(testValidatorProject));
    }
}
