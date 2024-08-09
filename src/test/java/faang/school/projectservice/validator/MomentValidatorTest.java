package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MomentValidatorTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private MomentValidator momentValidator;

    private Project project;

    @BeforeEach
    public void setUp() {
        momentValidator = new MomentValidator(projectRepository);
        project = Project.builder()
                .id(1L)
                .name("test")
                .status(ProjectStatus.CANCELLED)
                .build();
    }

    @Test
    public void testValidateCancelledProject() {
        Mockito.when(projectRepository.existsById(1L)).thenReturn(true);
        Assert.assertThrows(DataValidationException.class, () -> {
            momentValidator.validateProject(project);
        });
    }

    @Test
    public void testValidateCompletedProject() {
        Mockito.when(projectRepository.existsById(1L)).thenReturn(true);
        project.setStatus(ProjectStatus.COMPLETED);
        Assert.assertThrows(DataValidationException.class, () -> {
            momentValidator.validateProject(project);
        });
    }

    @Test
    public void testValidateUnexistedProject() {
        Mockito.when(projectRepository.existsById(1L)).thenReturn(false);
        Assert.assertThrows(EntityNotFoundException.class, () -> momentValidator.validateProject(project));
    }

    @Test
    public void testValidateProjectWithNullName() {
        Mockito.when(projectRepository.existsById(1L)).thenReturn(true);
        project.setName(null);
        Assert.assertThrows(DataValidationException.class, () -> {
            momentValidator.validateProject(project);
        });
    }

    @Test
    public void testValidateProjectWithBlankName() {
        project.setName("");
        Mockito.when(projectRepository.existsById(1L)).thenReturn(true);
        Assert.assertThrows(DataValidationException.class, () -> {
            momentValidator.validateProject(project);
        });
    }
}
