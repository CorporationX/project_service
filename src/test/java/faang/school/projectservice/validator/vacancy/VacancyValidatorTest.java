package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.model.dto.vacancy.VacancyDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.TeamRole;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyValidatorTest {

    @InjectMocks
    private VacancyValidator vacancyValidator;

    @Mock
    private ProjectJpaRepository projectRepository;

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    private final long projectId = 1L;
    private TeamMember curator;
    private VacancyDto vacancyDto;
    private TeamMember candidate1;
    private TeamMember candidate2;

    @Test
    void validateProject_whenProjectNotFound_shouldThrowException() {
        // given
        when(projectRepository.existsById(projectId)).thenReturn(false);
        // when & then
        assertThatThrownBy(() -> vacancyValidator.validateProject(projectId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project with ID: %d not found".formatted(projectId));
    }

    @Test
    void validateProject_whenProjectExists_shouldPass() {
        // given
        when(projectRepository.existsById(projectId)).thenReturn(true);
        // when/then
        vacancyValidator.validateProject(projectId);
    }

    @Test
    void validateCurator_whenCuratorDoesNotHaveManagerOrOwnerRole_shouldThrowException() {
        // given
        curator = TeamMember.builder().roles(List.of(TeamRole.DEVELOPER)).build();
        // when/then
        assertThatThrownBy(() -> vacancyValidator.validateCurator(curator))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Team member with ID: null does not have enough rights to create vacancy.");
    }

    @Test
    void validateCurator_whenCuratorHasManagerRole_shouldPass() {
        // given
        curator = TeamMember.builder().roles(List.of(TeamRole.MANAGER)).build();
        // when/then
        vacancyValidator.validateCurator(curator);
    }

    @Test
    void validateCurator_whenCuratorHasOwnerRole_shouldPass() {
        // given
        curator = TeamMember.builder().roles(List.of(TeamRole.OWNER)).build();
        // when/then
        vacancyValidator.validateCurator(curator);
    }

    @Test
    void validateCandidates_whenNotAllCandidatesAreQualified_shouldThrowException() {
        // given
        vacancyDto = VacancyDto.builder().candidateIds(List.of(1L, 2L)).build();
        candidate1 = TeamMember.builder().roles(List.of(TeamRole.DEVELOPER)).build();
        candidate2 = TeamMember.builder().roles(List.of(TeamRole.INTERN)).build();
        when(teamMemberRepository.findAllById(vacancyDto.candidateIds()))
                .thenReturn(List.of(candidate1, candidate2));
        // when/then
        assertThatThrownBy(() -> vacancyValidator.validateCandidates(vacancyDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not all candidates have the required roles for closing the vacancy.");
    }

    @Test
    void validateCandidates_whenAllCandidatesAreQualified_shouldPass() {
        // given
        vacancyDto = VacancyDto.builder().candidateIds(List.of(1L, 2L)).build();
        candidate1 = TeamMember.builder().roles(List.of(TeamRole.DEVELOPER)).build();
        candidate2 = TeamMember.builder().roles(List.of(TeamRole.ANALYST)).build();
        when(teamMemberRepository.findAllById(vacancyDto.candidateIds()))
                .thenReturn(List.of(candidate1, candidate2));
        // when/then
        vacancyValidator.validateCandidates(vacancyDto);
    }
}