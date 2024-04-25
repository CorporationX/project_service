package faang.school.projectservice.service;


import faang.school.projectservice.dto.Vacancy.VacancyDto;
import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.vacancy.VacancyFilter;
import faang.school.projectservice.service.validator.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyMapper vacancyMapper;
    private final List<VacancyFilter> vacancyFilters;
    private final VacancyValidator vacancyValidator;

    public VacancyDto createVacancy(long projectId, VacancyDto vacancyDto) {
        vacancyValidator.canCreateOrThrowException(vacancyDto);
        Project project = projectRepository.getProjectById(projectId);
        vacancyValidator.checkProjectStatus(project);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    public VacancyDto updateVacancy(Long vacancyId, VacancyDto vacancyDto) {
        Vacancy vacancy = getVacancy(vacancyId);
        Project project = vacancy.getProject();
        vacancyMapper.updateVacancy(vacancy, vacancyDto);
        int neededCount = vacancy.getCount();
        int count = countTeamMembersWithVacancyPosition(project, vacancy.getPosition());
        if (count >= neededCount) {
            vacancy.setStatus(VacancyStatus.CLOSED);
        }
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    private int countTeamMembersWithVacancyPosition(Project project, TeamRole position) {
        int count = 0;
        List<Team> teams = project.getTeams();
        for (Team team : teams) {
            List<TeamMember> teamMembers = team.getTeamMembers();
            for (TeamMember teamMember : teamMembers) {
                if (teamMember.getRoles().contains(position)) {
                    count++;
                }
            }
        }
        return count;
    }

    public VacancyDto getVacancyById(long vacancyId) {
        Vacancy vacancy = getVacancy(vacancyId);
        return vacancyMapper.toDto(vacancy);
    }

    public void deleteVacancy(long vacancyId) {
        Vacancy vacancy = getVacancy(vacancyId);
        List<Candidate> candidateList = vacancy.getCandidates();
        for (Candidate candidate : candidateList) {
            if (!candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED)) {
                teamMemberRepository.deleteById(candidate.getUserId());
            }
        }
    }

    private Vacancy getVacancy(long vacancyId){
        return vacancyRepository.findById(vacancyId).orElseThrow(() ->
                new EntityNotFoundException("Vacancy with id " + vacancyId + " does not exist"));
    }

    public List<VacancyDto> getVacancies(VacancyFilterDto filters) {
        Stream<Vacancy> vacancyStream = vacancyRepository.findAll().stream();
        vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(vacancyStream, filters));
        return vacancyMapper.toDto(vacancyStream.toList());
    }
}
