package faang.school.projectservice.validator.meet;

import faang.school.projectservice.exception.DeniedInAccessException;
import faang.school.projectservice.model.meet.Meet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MeetValidatorTest {

    private MeetValidator meetValidator;

    private Meet meet;

    @BeforeEach
    public void setUp() {
        long meetId = 1L;
        long meetCreator = 2L;
        meet = Meet.builder()
                .id(meetId)
                .createdBy(meetCreator)
                .build();
        meetValidator = new MeetValidator();
    }

    @Test
    @DisplayName("testing verifyUserIsCreatorOfMeet method with non appropriate value")
    public void testVerifyUserIsCreatorOfMeetWithNonAppropriateValue() {
        long userId = 3L;
        assertThrows(DeniedInAccessException.class, () -> meetValidator.verifyUserIsCreatorOfMeet(userId, meet));
    }

    @Test
    @DisplayName("testing verifyUserIsCreatorOfMeet method with appropriate value")
    public void testVerifyUserIsCreatorOfMeetWithAppropriateValue() {
        long userId = 2L;
        assertDoesNotThrow(() -> meetValidator.verifyUserIsCreatorOfMeet(userId, meet));
    }
}