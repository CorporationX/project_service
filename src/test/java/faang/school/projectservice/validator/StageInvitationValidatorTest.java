package faang.school.projectservice.validator;

import faang.school.projectservice.dto.stageInvitation.AcceptStageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.CreateStageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationValidatorTest {
    @InjectMocks
    private StageInvitationValidator stageInvitationValidator;

    @Mock
    private StageInvitationJpaRepository stageInvitationJpaRepository;

    private CreateStageInvitationDto createStageInvitationDto;
    private AcceptStageInvitationDto acceptStageInvitationDto;
    private TeamMember teamMember;
    private Stage stage;

    @BeforeEach
    public void setUp() {
        stage = Stage.builder().stageId(1L).stageName("Name").build();
        teamMember = TeamMember.builder().id(2L).userId(1L).build();
        createStageInvitationDto = CreateStageInvitationDto.builder().id(1L).stageId(1L).authorId(1L).invitedId(1L).build();
        acceptStageInvitationDto = AcceptStageInvitationDto.builder().id(1L).build();
    }

    @Test
    public void testCorrectWorkCreateValidationService() {
        assertDoesNotThrow(() -> stageInvitationValidator.createValidationService(stage, teamMember));
    }

    @Test
    public void testCreateValidationServiceWithRecurringUser() {
        when(stageInvitationJpaRepository.existsByInvitedAndStage(teamMember, stage)).
                thenReturn(true);
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.createValidationService(stage, teamMember));
    }

    @Test
    public void testCorrectWorkAcceptOrRejectInvitationService() {
        when(stageInvitationJpaRepository.existsById(createStageInvitationDto.getId())).thenReturn(true);
        assertDoesNotThrow(() -> stageInvitationValidator.acceptOrRejectInvitationService(acceptStageInvitationDto.getId()));
    }

    @Test
    public void testCreateValidationServiceWithNonExistentInvitation() {
        when(stageInvitationJpaRepository.existsById(createStageInvitationDto.getId())).thenReturn(false);
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.acceptOrRejectInvitationService(acceptStageInvitationDto.getId()));
    }
}
