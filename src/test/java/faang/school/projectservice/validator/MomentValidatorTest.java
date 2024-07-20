package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.MomentJpaRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static faang.school.projectservice.util.TestMoment.MOMENT_ID_1;
import static faang.school.projectservice.util.TestMomentDto.MOMENT_DTO;
import static faang.school.projectservice.util.TestMomentDto.MOMENT_DTO_EMPTY_PROJECT;
import static faang.school.projectservice.util.TestMomentDto.MOMENT_DTO_MANY_PROJECT;
import static faang.school.projectservice.util.TestProject.CANCELLED_PROJECTS;
import static faang.school.projectservice.util.TestProject.COMPLETED_PROJECTS;
import static faang.school.projectservice.util.TestProject.PROJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentValidatorTest {
    @Mock
    private MomentJpaRepository momentRepository;
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private MomentValidator momentValidator;

    @Test
    public void testMomentIsExists() {
        when(momentRepository.existsById(anyLong())).thenReturn(false);
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> momentValidator.existsMoment(MOMENT_ID_1));
        assertEquals("Moment not exists!", entityNotFoundException.getMessage());
    }

    @Test
    public void testMomentValidateIsProjectsEmpty() {
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> momentValidator.validateMoment(MOMENT_DTO_EMPTY_PROJECT, Collections.emptyList())
        );
        assertEquals("Projects empty.", dataValidationException.getMessage());
    }

    @Test
    public void testMomentValidateIsProjectsSizeAndMomenDtoGetProjectsIdSizeNotEquals() {
        lenient().when(projectRepository.findAllByIds(anyList()))
                .thenReturn(List.of(PROJECT));
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> momentValidator.validateMoment(MOMENT_DTO_MANY_PROJECT, Collections.emptyList())
        );
        assertEquals("Project does not exist.", dataValidationException.getMessage());
    }

    @Test
    public void testMomentValidateIsProjectStatusCompleted() {
        lenient().when(projectRepository.findAllByIds(anyList())).thenReturn(COMPLETED_PROJECTS);
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> momentValidator.validateMoment(MOMENT_DTO, COMPLETED_PROJECTS)
        );
        assertEquals("Project can`t be completed or canceled.",
                dataValidationException.getMessage());
    }

    @Test
    public void testMomentValidateIsProjectStatusCancelled() {
        lenient().when(projectRepository.findAllByIds(anyList())).thenReturn(CANCELLED_PROJECTS);
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> momentValidator.validateMoment(MOMENT_DTO, CANCELLED_PROJECTS)
        );
        assertEquals("Project can`t be completed or canceled.",
                dataValidationException.getMessage());
    }
}