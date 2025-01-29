package faang.school.projectservice.service;

import faang.school.projectservice.adapter.CandidateRepositoryAdapter;
import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.adapter.TeamMemberRepositoryAdapter;
import faang.school.projectservice.adapter.VacancyRepositoryAdapter;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.VacancyDTO;
import faang.school.projectservice.exception.BadRequestException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.repository.specification.VacancySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyMapper vacancyMapper;
    private final VacancyRepository vacancyRepository;
    private final ProjectRepositoryAdapter projectRepositoryAdapter;
    private final TeamMemberRepositoryAdapter teamMemberRepositoryAdapter;
    private final UserContext userContext;
    private final VacancyRepositoryAdapter vacancyRepositoryAdapter;
    private final CandidateRepositoryAdapter candidateRepositoryAdapter;

    @Transactional
    public VacancyDTO create(VacancyDTO vacancyDTO) {
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDTO);
        Project project = projectRepositoryAdapter.getById(vacancyDTO.getProjectId());
        TeamMember teamMember = teamMemberRepositoryAdapter
                .getByUserIdAndProjectId(userContext.getUserId(), project.getId());
        validateTeamMember(teamMember);
        vacancy.setProject(project);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(vacancy);
    }

    @Transactional
    public VacancyDTO update(Long id, VacancyDTO vacancyDTO) {
        Vacancy vacancy = vacancyRepositoryAdapter.getById(id);
        TeamMember teamMember = teamMemberRepositoryAdapter
                .getByUserIdAndProjectId(userContext.getUserId(), vacancy.getProject().getId());
        validateTeamMember(teamMember);
        vacancyMapper.update(vacancyDTO, vacancy);
        if (vacancyDTO.getCandidateIds() != null) {
            List<Candidate> candidates = candidateRepositoryAdapter.getByIds(vacancyDTO.getCandidateIds());
            vacancy.setCandidates(candidates);
            candidates.forEach(c -> c.setVacancy(vacancy));
        }
        if (Objects.equals(vacancyDTO.getStatus(), VacancyStatus.CLOSED)) {
            List<Candidate> acceptedCandidates = vacancy.getCandidates().stream()
                    .filter(c -> c.getCandidateStatus() == CandidateStatus.ACCEPTED).toList();
            if (acceptedCandidates.size() != vacancy.getCount()) {
                throw new BadRequestException("Please choose final candidates");
            } else {
                Team team = teamMember.getTeam();
                List<TeamMember> teamMembers = team.getTeamMembers();
                for (Candidate candidate : acceptedCandidates) {
                    boolean hasMatchingMember = teamMembers.stream()
                            .anyMatch(t -> t.getUserId().equals(candidate.getUserId()) &&
                                    t.getRoles().contains(vacancy.getPosition()));

                    if (!hasMatchingMember) {
                        throw new BadRequestException("Unable to close vacancy, " +
                                "team member doesn't have role in team");
                    }
                }
                vacancy.setStatus(VacancyStatus.CLOSED);
            }
        } else {
            if (vacancyDTO.getStatus() != null) {
                vacancy.setStatus(vacancyDTO.getStatus());
            }
        }
        vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(vacancy);

    }

    public VacancyDTO getById(Long id) {
        Vacancy vacancy = vacancyRepositoryAdapter.getById(id);
        return vacancyMapper.toDto(vacancy);
    }

    @Transactional
    public void deleteById(Long id) {
        Vacancy vacancy = vacancyRepositoryAdapter.getById(id);
        candidateRepositoryAdapter.deleteAllCandidatesByVacancy(vacancy.getCandidates());
        vacancyRepository.delete(vacancy);
    }

    public List<VacancyDTO> getAll(TeamRole position, String name) {
        List<Specification<Vacancy>> specs = new ArrayList<>();

        if (position != null) {
            specs.add(VacancySpecification.getByPosition(position));
        }
        if (name != null) {
            specs.add(VacancySpecification.getByName(name));
        }

        Specification<Vacancy> spec = specs.stream()
                .reduce(Specification::and)
                .orElse(null);

        List<Vacancy> goalInvitations = vacancyRepository.findAll(spec);
        return vacancyMapper.toDtoList(goalInvitations);
    }

    private void validateTeamMember(TeamMember teamMember) {
        if (!(teamMember.getRoles().contains(TeamRole.OWNER)
                || teamMember.getRoles().contains(TeamRole.MANAGER))) {
            throw new BadRequestException("team member must have roles " + TeamRole.OWNER + " or " + TeamRole.MANAGER);
        }
    }

}
