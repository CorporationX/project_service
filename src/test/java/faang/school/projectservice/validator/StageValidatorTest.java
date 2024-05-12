package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.validation.StageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageValidatorTest {
    @InjectMocks
    private StageValidator stageValidator;

    @Mock
    private StageJpaRepository stageJpaRepository;

    private Long id;

    @BeforeEach
    public void setUp() {
        id = 1L;
    }

    @Test
    public void testCorrectWorkExistById() {
        when(stageJpaRepository.existsById(id)).thenReturn(true);
        assertDoesNotThrow(() -> stageValidator.existsById(id));
    }

    @Test
    public void testExistByIdWithNonExistentStage() {
        when(stageJpaRepository.existsById(id)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> stageValidator.existsById(id));
    }
}
