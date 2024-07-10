package faang.school.projectservice.validator;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentServiceValidatorTest {

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private MomentServiceValidator momentServiceValidator;

    private MomentDto momentDto;
    private Project project;

    @BeforeEach
    public void setUp() {
        momentDto = new MomentDto();
        momentDto.setId(1L);
        momentDto.setProjectsIDs(List.of(1L));

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setStatus(ProjectStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should throw exception when moment already exists")
    public void testValidateCreateMoment_MomentExists() {
        when(momentRepository.existsById(1L)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> {
            momentServiceValidator.validateCreateMoment(momentDto);
        });
    }

    @Test
    @DisplayName("Should throw exception when project is cancelled")
    public void testValidateCreateMoment_ProjectCancelled() {
        when(momentRepository.existsById(1L)).thenReturn(false);
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        assertThrows(DataValidationException.class, () -> {
            momentServiceValidator.validateCreateMoment(momentDto);
        });
    }

    @Test
    @DisplayName("Should throw exception when project is completed")
    public void testValidateCreateMoment_ProjectCompleted() {
        when(momentRepository.existsById(1L)).thenReturn(false);
        project.setStatus(ProjectStatus.COMPLETED);
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        assertThrows(DataValidationException.class, () -> {
            momentServiceValidator.validateCreateMoment(momentDto);
        });
    }

    @Test
    @DisplayName("Should validate successfully for a valid moment and project")
    public void testValidateCreateMoment_Valid() {
        when(momentRepository.existsById(1L)).thenReturn(false);
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        momentServiceValidator.validateCreateMoment(momentDto);
    }
}