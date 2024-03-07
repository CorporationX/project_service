package faang.school.projectservice.validator;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class StageInvitationValidatorTest {
    @Mock
    private TeamMemberRepository tmRepository;
    @InjectMocks
    private StageInvitationValidator stageInvitationValidator;

    @Test
    public void testValidateStageInvitationWhenThrowException() {
        StageInvitationDto stageInvitationDto = StageInvitationDto.builder()
                .authorId(1L)
                .invitedId(1L)
                .build();
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> stageInvitationValidator.validateStageInvitation(stageInvitationDto));
        assertEquals(dataValidationException.getMessage(), "Invited can't be author!");
    }
}
