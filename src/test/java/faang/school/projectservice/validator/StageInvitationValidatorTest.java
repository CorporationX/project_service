package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.InvitationFilterDto;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.validation.StageInvitationValidator;
import faang.school.projectservice.validation.StageValidator;
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

    @Mock
    private StageValidator stageValidator;

    private StageInvitationDto stageInvitationDto;
    private StageInvitationDto stageInvitationDtoWithNullAuthor;
    private StageInvitationDto stageInvitationDtoWithNullStage;
    private StageInvitationDto stageInvitationDtoWithNullInvited;
    private InvitationFilterDto invitationFilterDto;
    private InvitationFilterDto nullInvitationFilterDto;
    private String explanation;
    private String nullExplanation;
    private String emptyExplanation;
    private Long userId;
    private Long nullUserId;

    @BeforeEach
    public void setUp() {
        Stage stage = Stage.builder().stageId(1L).stageName("Name").build();
        TeamMember teamMember = TeamMember.builder().id(2L).userId(1L).build();
        stageInvitationDto = StageInvitationDto.builder().id(1L).stage(stage).author(teamMember).invited(teamMember).build();
        nullInvitationFilterDto = InvitationFilterDto.builder().build();
        stageInvitationDtoWithNullStage = StageInvitationDto.builder().id(1L).author(teamMember).invited(teamMember).build();
        stageInvitationDtoWithNullAuthor = StageInvitationDto.builder().id(1L).stage(stage).invited(teamMember).build();
        stageInvitationDtoWithNullInvited = StageInvitationDto.builder().id(1L).stage(stage).author(teamMember).build();
        invitationFilterDto = InvitationFilterDto.builder().stage(stage).author(teamMember).status(StageInvitationStatus.PENDING).build();
        explanation = "explanation";
        nullExplanation = null;
        emptyExplanation = "";
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
    public void testAcceptInvitationValidationControllerWithNullStage() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                acceptInvitationValidationController(stageInvitationDtoWithNullStage));
    }

    @Test
    public void testAcceptInvitationValidationControllerWithNullInvited() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                acceptInvitationValidationController(stageInvitationDtoWithNullInvited));
    }

    @Test
    public void testCorrectWorkRejectInvitationValidationController() {
        assertDoesNotThrow(() -> stageInvitationValidator.rejectInvitationValidationController(explanation, stageInvitationDto));
    }

    @Test
    public void testRejectInvitationValidationControllerWithNullExplanation() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                rejectInvitationValidationController(nullExplanation, stageInvitationDto));
    }

    @Test
    public void testRejectInvitationValidationControllerWithEmptyExplanation() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                rejectInvitationValidationController(emptyExplanation, stageInvitationDto));
    }

    @Test
    public void testRejectInvitationValidationControllerWithNullStage() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                rejectInvitationValidationController(explanation, stageInvitationDtoWithNullStage));
    }

    @Test
    public void testRejectInvitationValidationControllerWithNullInvited() {
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.
                rejectInvitationValidationController(explanation, stageInvitationDtoWithNullInvited));
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
        assertDoesNotThrow(() -> stageInvitationValidator.createValidationService(stageInvitationDto));
    }

    @Test
    public void testCreateValidationServiceWithRecurringUser() {
        when(stageInvitationJpaRepository.existsByInvitedAndStage(stageInvitationDto.getInvited(), stageInvitationDto.getStage())).
                thenReturn(true);
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.createValidationService(stageInvitationDto));
    }

    @Test
    public void testCreateValidationServiceWithNonExistentStage() {
        doThrow(DataValidationException.class).when(stageValidator).existsById(stageInvitationDto.getStage().getStageId());
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.createValidationService(stageInvitationDto));
    }

    @Test
    public void testCorrectWorkAcceptOrRejectInvitationService() {
        assertDoesNotThrow(() -> stageInvitationValidator.acceptOrRejectInvitationService(stageInvitationDto));
    }

    @Test
    public void testCreateValidationServiceWithNonExistentInvitation() {
        when(stageInvitationJpaRepository.existsById(stageInvitationDto.getId())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.acceptOrRejectInvitationService(stageInvitationDto));
    }
}
