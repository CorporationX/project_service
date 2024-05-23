package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class StageInvitationValidatorTest {
    @InjectMocks
    private StageInvitationValidator stageInvitationValidator;

    @Test
    public void testValidateIdWithCorrectValues() {
        Long id = 1L;
        assertDoesNotThrow(() -> stageInvitationValidator.validateId(id));
    }

    @Test
    public void testValidateIdWithNull() {
        Long id = null;
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.validateId(id));
    }

    @Test
    public void testValidateIdLessThanOrEqualZero() {
        Long id = 0L;
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.validateId(id));
    }

    @Test
    public void testValidateDescriptionWithCorrectValues() {
        String description = "text";
        assertDoesNotThrow(() -> stageInvitationValidator.validateDescription(description));
    }

    @Test
    public void testValidateDescriptionWithNull() {
        String description = null;
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.validateDescription(description));
    }

    @Test
    public void testValidateDescriptionWithEmpty() {
        String description = "     ";
        assertThrows(DataValidationException.class, () -> stageInvitationValidator.validateDescription(description));
    }
}