package faang.school.projectservice.validator.meet;

import faang.school.projectservice.model.entity.meet.Meet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeetValidatorTest {

    private final MeetValidator meetValidator = new MeetValidator();

    private Meet validMeet;

    @BeforeEach
    void setUp() {
        validMeet = Meet.builder()
                .id(1L)
                .title("Team Meeting")
                .creatorId(1L)
                .build();
    }

    @Test
    void validateMeetToUpdate_shouldNotThrowException_whenCreatorIdMatches() {
        assertDoesNotThrow(() -> meetValidator.validateMeetToUpdate(validMeet, 1L));
    }

    @Test
    void validateMeetToUpdate_shouldThrowException_whenCreatorIdDoesNotMatch() {
        assertThrows(RuntimeException.class, () -> meetValidator.validateMeetToUpdate(validMeet, 2L),
                "Only creator can update meet");
    }

    @Test
    void validateMeetToDelete_shouldNotThrowException_whenCreatorIdMatches() {
        assertDoesNotThrow(() -> meetValidator.validateMeetToDelete(validMeet, 1L));
    }

    @Test
    void validateMeetToDelete_shouldThrowException_whenCreatorIdDoesNotMatch() {
        assertThrows(RuntimeException.class, () -> meetValidator.validateMeetToDelete(validMeet, 2L),
                "Only creator can delete meet and participants");
    }
}