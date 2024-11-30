package faang.school.projectservice.validator;

import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberValidatorTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberValidator teamMemberValidator;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
    }

    @Test
    @DisplayName("Test team member exists by id: success")
    void validateTeamMemberExistsById_ValidId_Success() {
        when(teamMemberRepository.existsById(userId)).thenReturn(true);

        assertDoesNotThrow(() -> teamMemberValidator.validateTeamMemberExistsById(userId));
    }

    @Test
    @DisplayName("Test team member doesn't exist: fail")
    void validateTeamMemberExistsById_InvalidId_Fail() {
        when(teamMemberRepository.existsById(userId)).thenReturn(false);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> teamMemberValidator.validateTeamMemberExistsById(userId));
        assertEquals(String.format("Team member not found, id: %d", userId), ex.getMessage());
    }
}