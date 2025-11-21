package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.vacancy.EntityNotFoundException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;
    private final VacancyRepository vacancyRepository;

    @Transactional
    public TeamMember addCandidateToTeam(Long projectId, Long candidateId, Long vacancyId) {
        long userId = userContext.getUserId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        VacancyValidator.validateRole(author);

        Candidate candidate = vacancyRepository.getByIdOrThrow(vacancyId).getCandidates().stream()
                .filter(x -> x.getId().equals(candidateId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Candidate with id %s not found in vacancy %s", candidateId, vacancyId)));

        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);
        Project project = projectRepository.findByIdOrThrow(projectId);
        VacancyValidator.validateCandidateIsAlreadyProjectMember(project, candidate);
        Team team = project.getTeams().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Project has no teams"));

        TeamMember newMember = TeamMember.builder()
                .userId(candidate.getUserId())
                .nickname(candidate.getUsername())
                .roles(List.of(vacancy.getPosition()))
                .team(team)
                .build();
        return teamMemberRepository.save(newMember);
    }

    @Transactional(propagation=MANDATORY)
    public void removeMemberFromTeam(TeamMember teamMember) {
        validationTeamMemberIsNotEmpty(teamMember);
        teamMemberRepository.delete(teamMember);
    }

    private void validationTeamMemberIsNotEmpty(TeamMember teamMember) {
        if (teamMember == null) {
            throw new EntityNotFoundException("Team member can't be empty");
        }
    }
}
