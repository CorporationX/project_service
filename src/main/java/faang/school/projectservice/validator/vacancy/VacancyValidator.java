package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.model.dto.vacancy.VacancyDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.TeamRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VacancyValidator {

    private final ProjectJpaRepository projectRepository;
    private final TeamMemberJpaRepository teamMemberRepository;

    public void validateProject(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project with ID: %d not found".formatted(projectId));
        }
    }

    public void validateCurator(TeamMember curator) {
        if (!(curator.getRoles().contains(TeamRole.MANAGER) || curator.getRoles().contains(TeamRole.OWNER))) {
            throw new RuntimeException("Team member with ID: %d does not have enough rights to create vacancy.".formatted(curator.getId()));
        }
    }

    public void validateCandidates(VacancyDto dto) {
        List<TeamMember> candidates = teamMemberRepository.findAllById(dto.candidateIds());
        boolean allQualified = candidates.stream()
                .allMatch(candidate -> candidate.getRoles().contains(TeamRole.DEVELOPER) || candidate.getRoles().contains(TeamRole.ANALYST));
        if (!allQualified) {
            throw new IllegalStateException("Not all candidates have the required roles for closing the vacancy.");
        }
    }
}