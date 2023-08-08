package faang.school.projectservice.validator;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentValidatorTest {
    private static final Long PROJECT_ID = 1L;
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private MomentValidator validator;
    private MomentDto momentDto;

    @BeforeEach
    void setUp() {
        momentDto = MomentDto.builder()
                .projectIds(List.of(PROJECT_ID))
                .build();

        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
    }

    @Test
    void validateMomentProjects_shouldInvokeProjectRepositoryExistsById() {
        validator.validateMomentProjects(momentDto);
        verify(projectRepository).existsById(PROJECT_ID);
    }

    @Test
    void validateMomentProjects_shouldThrowEntityNotFoundException() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,
                () -> validator.validateMomentProjects(momentDto),
                "Project with id " + PROJECT_ID + " does not exist");
    }

    @Test
    void validateMomentProjects_shouldInvokeProjectRepositoryFindAllByIds() {
        validator.validateMomentProjects(momentDto);
        verify(projectRepository).findAllByIds(momentDto.getProjectIds());
    }

    @Test
    void validateMomentProjects_shouldThrowDataValidationException() {
        Project project = Project.builder()
                .id(PROJECT_ID)
                .status(ProjectStatus.CANCELLED)
                .build();

        when(projectRepository.findAllByIds(momentDto.getProjectIds())).thenReturn(List.of(project));

        assertThrows(DataValidationException.class,
                () -> validator.validateMomentProjects(momentDto),
                "Moment must not have closed projects");
    }

    @Test
    void validateMomentProjects_shouldNotThrowAnyException() {
        validator.validateMomentProjects(momentDto);
        assertDoesNotThrow(() -> validator.validateMomentProjects(momentDto));
    }
}