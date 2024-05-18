package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.InvitationFilterDto;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
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

    private StageInvitationDto stageInvitationDto;
    private StageInvitationDto stageInvitationDtoWithNullAuthor;
    private StageInvitationDto stageInvitationDtoWithNullStage;
    private StageInvitationDto stageInvitationDtoWithNullInvited;
    private StageInvitationDto stageInvitationDtoWithNullExplanation;
    private StageInvitationDto stageInvitationDtoWithEmptyExplanation;
    private InvitationFilterDto invitationFilterDto;
    private InvitationFilterDto nullInvitationFilterDto;
    private Long userId;
    private Long nullUserId;
    private TeamMember teamMember;
    private Stage stage;

    @BeforeEach
    public void setUp() {
        stage = Stage.builder().stageId(1L).stageName("Name").build();
        teamMember = TeamMember.builder().id(2L).userId(1L).build();
        stageInvitationDto = StageInvitationDto.builder().id(1L).stageId(1L).authorId(1L).invitedId(1L).explanation("text").build();
        nullInvitationFilterDto = InvitationFilterDto.builder().build();
        stageInvitationDtoWithNullStage = StageInvitationDto.builder().id(1L).authorId(1L).invitedId(1L).build();
        stageInvitationDtoWithNullAuthor = StageInvitationDto.builder().id(1L).stageId(1L).invitedId(1L).build();
        stageInvitationDtoWithNullInvited = StageInvitationDto.builder().id(1L).stageId(1L).authorId(1L).build();
        stageInvitationDtoWithNullExplanation = StageInvitationDto.builder().id(1L).stageId(1L).authorId(1L).explanation(null).build();
        stageInvitationDtoWithEmptyExplanation = StageInvitationDto.builder().id(1L).stageId(1L).authorId(1L).explanation(" ").build();
        invitationFilterDto = InvitationFilterDto.builder().stageId(1L).authorId(1L).status(StageInvitationStatus.PENDING).build();
        userId = 1L;
        nullUserId = null;
    }

    @Test
    public void testCorrectWorkCreateValidationControllerWithNullStage() {
        assertDoesNotThrow(() -> stageInvitationValidator.createValidationController(stageInvitationDto));
    }

    @Test
    public void  testCreateValidationControllerWithNullStage() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                createValidationController(stageInvitationDtoWithNullStage));
    }

    @Test
    public void  testCreateValidationControllerWithNullInvited() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                createValidationController(stageInvitationDtoWithNullInvited));
    }

    @Test
    public void  testCreateValidationControllerWithNullAuthor() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                createValidationController(stageInvitationDtoWithNullAuthor));
    }

    @Test
    public void testCorrectWorkAcceptInvitationValidationController() {
        assertDoesNotThrow(() -> stageInvitationValidator.acceptInvitationValidationController(stageInvitationDto));
    }

    @Test
    public void testCorrectWorkRejectInvitationValidationController() {
        assertDoesNotThrow(() -> stageInvitationValidator.rejectInvitationValidationController(stageInvitationDto));
    }

    @Test
    public void testRejectInvitationValidationControllerWithNullExplanation() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                rejectInvitationValidationController(stageInvitationDtoWithNullExplanation));
    }

    @Test
    public void testRejectInvitationValidationControllerWithEmptyExplanation() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                rejectInvitationValidationController(stageInvitationDtoWithEmptyExplanation));
    }

    @Test
    public void testRejectInvitationValidationControllerWithNullStage() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                rejectInvitationValidationController(stageInvitationDtoWithNullStage));
    }

    @Test
    public void testRejectInvitationValidationControllerWithNullInvited() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                rejectInvitationValidationController(stageInvitationDtoWithNullInvited));
    }

    @Test
    public void testCorrectWorkShowAllInvitationForMemberValidationController() {
        assertDoesNotThrow(() -> stageInvitationValidator.showAllInvitationForMemberValidationController(userId, invitationFilterDto));
    }

    @Test
    public void testShowAllInvitationForMemberValidationControllerWithNullInvited() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                showAllInvitationForMemberValidationController(nullUserId, invitationFilterDto));
    }

    @Test
    public void testShowAllInvitationForMemberValidationControllerWithNullFilter() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                showAllInvitationForMemberValidationController(nullUserId, nullInvitationFilterDto));
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
        when(stageInvitationJpaRepository.existsById(stageInvitationDto.getId())).thenReturn(true);
        assertDoesNotThrow(() -> stageInvitationValidator.acceptOrRejectInvitationService(stageInvitationDto));
    }

    @Test
    public void testCreateValidationServiceWithNonExistentInvitation() {
        when(stageInvitationJpaRepository.existsById(stageInvitationDto.getId())).thenReturn(false);
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.acceptOrRejectInvitationService(stageInvitationDto));
    }
}
