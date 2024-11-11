package validation;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.validator.StageInvitationValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StageInvitationValidatorTest {

    @InjectMocks
    private StageInvitationValidator validator;
    private final StageInvitationDto invitationDto = new StageInvitationDto();

    @Test
    public void testValidateInvitation() {
        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.validateInvitation(invitationDto)
        );
    }

    @Test
    public void testValidateDescription() {
        invitationDto.setDescription(" ");
        Assertions.assertThrows(
                DataValidationException.class,
                () -> validator.validateDescription(invitationDto)
        );
    }
}
