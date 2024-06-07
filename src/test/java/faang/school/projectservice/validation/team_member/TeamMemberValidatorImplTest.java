package faang.school.projectservice.validation.team_member;

import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.repository.TeamMemberRepository;
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
class TeamMemberValidatorImplTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberValidatorImpl teamMemberValidator;

    private final long teamMemberId = 1L;

    @Test
    void validateExistence() {
        when(teamMemberRepository.existsById(teamMemberId)).thenReturn(true);

        assertDoesNotThrow(() -> teamMemberValidator.validateExistence(teamMemberId));
    }

    @Test
    void validateExistenceNotFound() {
        when(teamMemberRepository.existsById(teamMemberId)).thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class, () -> teamMemberValidator.validateExistence(teamMemberId));
        assertEquals("TeamMember with id=" + teamMemberId + " does not exist", e.getMessage());
    }
}