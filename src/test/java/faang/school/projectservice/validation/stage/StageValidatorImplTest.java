package faang.school.projectservice.validation.stage;

import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageValidatorImplTest {

    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private StageValidatorImpl stageValidator;

    private final long stageId = 1L;

    @Test
    void validateExistence() {
        when(stageRepository.existsById(stageId)).thenReturn(true);

        assertDoesNotThrow(() -> stageValidator.validateExistence(stageId));
    }

    @Test
    void validateExistenceNotFound() {
        when(stageRepository.existsById(stageId)).thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class, () -> stageValidator.validateExistence(stageId));
        assertEquals("Stage with id=" + stageId + " does not exist", e.getMessage());
    }
}