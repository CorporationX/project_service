package faang.school.projectservice;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class MomentValidatorTest {
    @InjectMocks
    MomentValidator momentValidator;

    @Test
    public void testValidateMomentWithBlankName() {
        Moment moment = new Moment();
        moment.setName(" ");

        Assertions.assertThrows(DataValidationException.class, () -> momentValidator.validateMoment(moment));
    }

    @Test
    public void testValidateMomentWithEmptyName() {
        Moment moment = new Moment();
        moment.setName("");

        Assertions.assertThrows(DataValidationException.class, () -> momentValidator.validateMoment(moment));
    }

    @Test
    public void testValidateMoment() {
        Moment moment = new Moment();
        moment.setName("test");

        assertDoesNotThrow(() -> momentValidator.validateMoment(moment));
    }
}
