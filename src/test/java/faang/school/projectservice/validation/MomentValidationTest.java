package faang.school.projectservice.validation;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.MomentJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentValidationTest {

    private static final long MOMENT_ID = 1;

    @Mock
    private MomentJpaRepository momentRepository;
    @InjectMocks
    private MomentValidation momentValidation;

    @Test
    public void testNameIsFilledThrowException() {
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> momentValidation.nameIsFilled(""));
        assertEquals("Name of moment not be null or empty",
                dataValidationException.getMessage());
    }

    @Test
    public void testMomentIsExists() {
        when(momentRepository.existsById(MOMENT_ID)).thenReturn(false);
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> momentValidation.existsMoment(MOMENT_ID));
        assertEquals("Moment not exists!", entityNotFoundException.getMessage());
    }
}