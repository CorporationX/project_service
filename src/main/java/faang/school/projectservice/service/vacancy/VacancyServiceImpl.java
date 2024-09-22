package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.VacancyFilter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService{

    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;
    private final CandidateRepository candidateRepository;
    private final TeamRepository teamRepository;
    private final List<VacancyFilter> vacancyFilters;
    private final VacancyMapper vacancyMapper;

    @Override
    @Transactional
    public VacancyDto create(VacancyDto vacancy) {
        Vacancy vacancyEntity = vacancyMapper.toEntity(vacancy);
        Project project = setProjectById(vacancy, vacancyEntity);

        assignRoleToCurator(vacancy.getCreatedBy(), project);
        isValidCuratorRole(vacancy.getCreatedBy(), project);

        vacancyRepository.save(vacancyEntity);

        return vacancy;
    }

    @Override
    @Transactional
    public VacancyDto update(VacancyDto vacancy) {
        if (vacancyRepository.findById(vacancy.getId()).isPresent()) {
            Vacancy vacancyEntity = vacancyMapper.toEntity(vacancy);
            setProjectById(vacancy, vacancyEntity);

            if (vacancyEntity.getStatus() == VacancyStatus.CLOSED) {
                validationRequiredCandidates(vacancy);
                validationCandidates(vacancy);
            }
            vacancyRepository.save(vacancyEntity);
        } else {
            throw new DataValidationException("Vacancies with given id do not exist");
        }

        return vacancy;
    }

    @Override
    @Transactional
    public VacancyDto delete(VacancyDto vacancy) {
        Project project = projectRepository.getProjectById(vacancy.getProjectId());

        for (Team team : project.getTeams()) {
            List<TeamMember> teamMembers = team.getTeamMembers();
            teamMembers.removeIf(teamMember -> teamMember.getRoles() == null);
            team.setTeamMembers(teamMembers);
            teamRepository.save(team);
        }

        Vacancy vacancyEntity = vacancyMapper.toEntity(vacancy);
        vacancyRepository.delete(vacancyEntity);

        return vacancy;
    }

    @Override
    public List<VacancyDto> getVacanciesByFilter(VacancyFilterDto filters) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        return vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(vacancies, filters))
                .map(vacancyMapper::toDto)
                .toList();
    }

    @Override
    public VacancyDto getVacancyById(VacancyDto vacancy) {
        Optional<Vacancy> vacancyEntity =  vacancyRepository.findById(vacancy.getId());
        if (vacancyEntity.isPresent()) {
            return vacancyMapper.toDto(vacancyEntity.get());
        } else {
            throw new DataValidationException("Vacancies with this id do not exist");
        }
    }

    private void assignRoleToCurator(Long curatorId, Project project) {
        for (Team team : project.getTeams()) {
            for (TeamMember teamMember : team.getTeamMembers()) {
                if (teamMember.getUserId().equals(curatorId)) {
                    teamMember.setRoles(List.of(TeamRole.OWNER));
                }
            }
        }
    }

    private void isValidCuratorRole(Long curatorId, Project project) {
        TeamMember teamMember = project.getTeams().stream()
                .flatMap(t -> t.getTeamMembers().stream())
                .filter(member -> member.getUserId().equals(curatorId))
                .findFirst()
                .orElse(null);

        if (teamMember == null || !teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new DataValidationException("The Curator must be a member of the team and have the role of owner!");
        }
    }

    private void validationCandidates(VacancyDto vacancy) {
        for (Long candidateId : vacancy.getCandidateIds()) {
            Optional<Candidate> candidate = candidateRepository.findById(candidateId);
            if (candidate.isPresent()) {
                Candidate candidateEntity = candidate.get();
                if (candidateEntity.getUserId() == null) {
                    throw new DataValidationException("User id is null");
                }

                if (candidateEntity.getResumeDocKey() == null || candidateEntity.getResumeDocKey().isBlank()) {
                    throw new DataValidationException("Resume is empty");
                }

                if (candidateEntity.getCandidateStatus() != CandidateStatus.WAITING_RESPONSE) {
                    throw new DataValidationException("You have already been reviewed");
                }

                if (vacancyRepository.findById(vacancy.getId()).isEmpty()) {
                    throw new DataValidationException("No such vacancy found in the system");
                }
            } else {
                throw new DataValidationException("There is no such candidate");
            }
        }
    }

    private void validationRequiredCandidates(VacancyDto vacancy) {
        if (vacancy.getCount() <= vacancy.getCandidateIds().size()) {
            throw new DataValidationException("You can't close a vacancy if there are fewer candidates than needed");
        }
    }

    private Project setProjectById(VacancyDto vacancyDto, Vacancy vacancyEntity) {
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
        vacancyEntity.setProject(project);

        return project;
    }

}
