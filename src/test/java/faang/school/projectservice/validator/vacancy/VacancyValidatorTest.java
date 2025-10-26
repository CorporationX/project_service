package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.exeption.ForbiddenException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
@ExtendWith(MockitoExtension.class)
public class VacancyValidatorTest {
    @InjectMocks
    private VacancyValidator vacancyValidator;

    @Test
    void validateRole_WithManagerRole_Success() {

        TeamMember teamMember = TeamMember.builder()
                .roles(List.of(TeamRole.MANAGER))
                .build();

        // should not throw exception
        assertDoesNotThrow(() -> vacancyValidator.validateRole(teamMember));
    }

    @Test
    void validateRole_WithOwnerRole_Success() {

        TeamMember teamMember = TeamMember.builder()
                .roles(List.of(TeamRole.OWNER))
                .build();

        // should not throw exception
        assertDoesNotThrow(() -> vacancyValidator.validateRole(teamMember));
    }

    @Test
    void validateRole_WithoutRequiredRole_ThrowsException() {

        TeamMember teamMember = TeamMember.builder()
                .roles(List.of(TeamRole.DEVELOPER)) // Not MANAGER or OWNER
                .build();


        assertThrows(ForbiddenException.class,
                () -> vacancyValidator.validateRole(teamMember));
    }
}

