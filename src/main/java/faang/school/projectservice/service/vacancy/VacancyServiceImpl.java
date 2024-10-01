package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.VacancyFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public VacancyDto create(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        Project project = setProjectById(vacancyDto, vacancy);

        assignRoleToCurator(vacancyDto.getCreatedBy(), project);
        isValidCuratorRole(vacancyDto.getCreatedBy(), project);

        vacancyRepository.save(vacancy);

        return vacancyDto;
    }

    @Override
    @Transactional
    public VacancyDto update(VacancyDto vacancyDto) {
        if (vacancyRepository.findById(vacancyDto.getId()).isPresent()) {
            Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
            setProjectById(vacancyDto, vacancy);

            if (vacancy.getStatus() == VacancyStatus.CLOSED) {
                validationRequiredCandidates(vacancyDto);
                validationCandidates(vacancyDto);
            }
            vacancyRepository.save(vacancy);
        } else {
            throw new DataValidationException("Vacancies with given id do not exist");
        }

        return vacancyDto;
    }

    @Override
    @Transactional
    public VacancyDto delete(VacancyDto vacancyDto) {
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());

        for (Team team : project.getTeams()) {
            List<TeamMember> teamMembers = team.getTeamMembers();
            teamMembers.removeIf(teamMember -> teamMember.getRoles() == null);
            team.setTeamMembers(teamMembers);
            teamRepository.save(team);
        }

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancyRepository.delete(vacancy);

        return vacancyDto;
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
    public VacancyDto getVacancyById(VacancyDto vacancyDto) {
        Vacancy vacancy =  vacancyRepository.findById(vacancyDto.getId())
                .orElseThrow(() -> new DataValidationException("Vacancy is null"));

        return vacancyMapper.toDto(vacancy);
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
        TeamMember teamMember = project.getTeams()
                .stream()
                .flatMap(t -> t.getTeamMembers().stream())
                .filter(member -> member.getUserId().equals(curatorId))
                .findFirst()
                .orElseThrow(() -> new DataValidationException("TeamMember is null"));

        if (!teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new DataValidationException("The Curator must be a member of the team and have the role of owner!");
        }
    }

    private void validationCandidates(VacancyDto vacancyDto) {
        for (Long candidateId : vacancyDto.getCandidateIds()) {
            Candidate candidate = candidateRepository.findById(candidateId)
                    .orElseThrow(() -> new DataValidationException("Candidate is null"));

            if (candidate.getUserId() == null) {
                throw new DataValidationException("User id is null");
            }

            if (candidate.getResumeDocKey() == null || candidate.getResumeDocKey().isBlank()) {
                throw new DataValidationException("Resume is empty");
            }

            if (candidate.getCandidateStatus() != CandidateStatus.WAITING_RESPONSE) {
                throw new DataValidationException("You have already been reviewed");
            }

        }
    }

    private void validationRequiredCandidates(VacancyDto vacancyDto) {
        vacancyDto.setStatus(VacancyStatus.OPEN);
        if (vacancyDto.getCount() > vacancyDto.getCandidateIds().size()) {
            throw new DataValidationException("You can't close a vacancy if there are fewer candidates than needed");
        }
    }

    private Project setProjectById(VacancyDto vacancyDto, Vacancy vacancy) {
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
        vacancy.setProject(project);

        return project;
    }

}
