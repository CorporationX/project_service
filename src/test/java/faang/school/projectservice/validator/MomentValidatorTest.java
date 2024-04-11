package faang.school.projectservice.validator;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentValidatorTest {
    @InjectMocks
    private MomentValidator momentValidator;
    @Mock
    private ProjectRepository projectRepository;

    @Test
    public void testEmptyProjectIds() {
        List<Long> projectIds = new ArrayList<>();
        MomentDto momentDto = MomentDto.builder()
                .projectIds(projectIds)
                .build();
        assertThrows(DataValidationException.class, () -> momentValidator.validateProjectIds(momentDto));
    }

    @Test
    public void testNotExistedProjectIds() {
        List<Long> projectIds = List.of(1L);
        MomentDto momentDto = MomentDto.builder()
                .projectIds(projectIds)
                .build();
        assertThrows(DataValidationException.class, () -> momentValidator.validateProjectIds(momentDto));
    }

    @Test
    public void testCompletedProjectIds() {
        List<Long> projectIds = List.of(1L);
        MomentDto momentDto = MomentDto.builder()
                .projectIds(projectIds)
                .build();
        when(projectRepository.findAllByIds(projectIds)).thenReturn(List.of(Project.builder().status(ProjectStatus.COMPLETED).build()));
        assertThrows(DataValidationException.class, () -> momentValidator.validateProjectIds(momentDto));
    }

    @Test
    public void testCancelledProjectIds() {
        List<Long> projectIds = List.of(1L);
        MomentDto momentDto = MomentDto.builder()
                .projectIds(projectIds)
                .build();
        when(projectRepository.findAllByIds(projectIds)).thenReturn(List.of(Project.builder().status(ProjectStatus.CANCELLED).build()));
        assertThrows(DataValidationException.class, () -> momentValidator.validateProjectIds(momentDto));
    }

    @Test
    public void testGoodProjectIds() {
        List<Long> projectIds = List.of(1L);
        MomentDto momentDto = MomentDto.builder()
                .projectIds(projectIds)
                .build();
        when(projectRepository.findAllByIds(List.of(1L))).thenReturn(List.of(Project.builder().id(1L).build()));
        assertDoesNotThrow(() -> momentValidator.validateProjectIds(momentDto));
    }
}
