package faang.school.projectservice.validator.meet;

import faang.school.projectservice.exception.DeniedInAccessException;
import faang.school.projectservice.jpa.MeetJpaRepository;
import faang.school.projectservice.model.meet.Meet;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetValidatorTest {

    @Mock
    private MeetJpaRepository meetJpaRepository;

    @InjectMocks
    private MeetValidator meetValidator;

    private long meetId;
    private Meet meet;

    @BeforeEach
    public void setUp() {
        meetId = 1L;
        long meetCreator = 2L;
        meet = Meet.builder()
                .id(meetId)
                .createdBy(meetCreator)
                .build();
    }

    @Test
    @DisplayName("testing verifyMeetExistence method with non appropriate value")
    public void testVerifyMeetExistenceWithNonAppropriateValue() {
        when(meetJpaRepository.findById(meetId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> meetValidator.verifyMeetExistence(meetId));
    }

    @Test
    @DisplayName("testing verifyMeetExistence method with appropriate value")
    public void testVerifyMeetExistenceWithAppropriateValue() {
        when(meetJpaRepository.findById(meetId)).thenReturn(Optional.of(meet));
        assertEquals(meet, meetValidator.verifyMeetExistence(meetId));
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