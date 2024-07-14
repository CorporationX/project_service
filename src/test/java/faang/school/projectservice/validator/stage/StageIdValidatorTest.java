package faang.school.projectservice.validator.stage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class StageIdValidatorTest {
    @InjectMocks
    private StageIdValidator stageIdValidator;

    private Long stageId;
    private Long replaceStageId;

    @BeforeEach
    public void setUp() {
        stageId = 1L;
        replaceStageId = 1L;

        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("test that validateReplaceId throws IllegalArgumentException when equals stage id and replace stage id was given")
    public void testValidateReplaceId() {
        assertThrows(IllegalArgumentException.class,
                () -> stageIdValidator.validateReplaceId(stageId, replaceStageId));
    }
}
