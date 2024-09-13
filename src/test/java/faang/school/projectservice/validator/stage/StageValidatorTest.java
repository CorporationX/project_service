package faang.school.projectservice.validator.stage;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class StageValidatorTest {

    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private StageValidator validator;

    @Test
    void testValidateProjectError(){
        when(stageRepository.existById(anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> validator.validateProject(1L));
    }

    @Test
    void testValidateProjectOk(){
        when(stageRepository.existById(anyLong())).thenReturn(true);

        validator.validateProject(1L);

        verify(stageRepository).existById(anyLong());
    }
}
