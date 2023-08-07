package faang.school.projectservice.validator;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidateInviteException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentValidatorTest {
    private static final Long PROJECT_ID = 1L;
    @Mock
    private MomentRepository momentRepository;
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
    }

    @Test
    void validateMomentProjects_shouldInvokeProjectRepositoryFindAllByIds() {
        validator.validateMomentProjects(momentDto);
        verify(projectRepository).findAllByIds(momentDto.getProjectIds());
    }

    @Test
    void validateMomentProjects_shouldThrowException() {
        Project project = Project.builder()
                .id(PROJECT_ID)
                .status(ProjectStatus.CANCELLED)
                .build();

        when(projectRepository.findAllByIds(momentDto.getProjectIds())).thenReturn(List.of(project));

        assertThrows(DataValidateInviteException.class,
                () -> validator.validateMomentProjects(momentDto),
                "Moment must not have closed projects");
    }
}