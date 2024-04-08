package faang.school.projectservice.service;


import faang.school.projectservice.dto.Vacancy.VacancyDto;
import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.vacancy.VacancyFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    public VacancyDto createVacancy(long projectId, VacancyDto vacancyDto, Long curatorId) {
        TeamMember curator = teamMemberRepository.findById(curatorId);
        List<TeamRole> canCreateVacancy = List.of(TeamRole.OWNER,
                TeamRole.MANAGER);
        if (null == curator ||
                Collections.disjoint(canCreateVacancy, curator.getRoles())) {
            throw new DataValidationException("This user can not create vacancy!");
        }
        Project project = projectRepository.getProjectById(projectId);
        if (project == null || project.getStatus().equals(ProjectStatus.CANCELLED) ||
                project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new DataValidationException("This project can not have vacancies!");
        }
        Vacancy vacancy = Vacancy.builder()
                .name(vacancyDto.getName())
                .description(vacancyDto.getDescription())
                .project(project)
                .status(vacancyDto.getVacancyStatus())
                .salary(vacancyDto.getSalary())
                .count(vacancyDto.getCount())
                .position(vacancyDto.getPosition())
                .build();
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    public VacancyDto updateVacancy(Long vacancyId, VacancyDto vacancyDto) {
        Vacancy vacancy = findVacancyOrThrowException(vacancyId);
        Project project = vacancy.getProject();
        vacancy.setCount(vacancyDto.getCount() == null ? vacancy.getCount() : vacancyDto.getCount());
        vacancy.setSalary(vacancyDto.getSalary() == null ? vacancy.getSalary() : vacancy.getSalary());
        vacancy.setName(vacancyDto.getName() == null ? vacancy.getName() : vacancyDto.getName());
        vacancy.setDescription(vacancyDto.getDescription() == null ? vacancy.getDescription() : vacancyDto.getDescription());
        vacancy.setPosition(vacancyDto.getPosition() == null ? vacancy.getPosition() : vacancyDto.getPosition());
        int neededCount = vacancy.getCount();
        int count = 0;
        List<Team> teams = project.getTeams();
        for (Team team : teams) {
            List<TeamMember> teamMembers = team.getTeamMembers();
            for (TeamMember teamMember : teamMembers) {
                if (teamMember.getRoles().contains(vacancy.getPosition())) {
                    count++;
                }
            }
        }
        if (count >= neededCount) {
            vacancy.setStatus(VacancyStatus.CLOSED);
        }
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    public VacancyDto getVacancyById(long vacancyId) {
        Vacancy vacancy = findVacancyOrThrowException(vacancyId);
        return vacancyMapper.toDto(vacancy);
    }

    public void deleteVacancy(long vacancyId) {
        Vacancy vacancy = findVacancyOrThrowException(vacancyId);
        List<Candidate> candidateList = vacancy.getCandidates();
        for (Candidate candidate : candidateList) {
            if (!candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED)) {
                teamMemberRepository.deleteById(candidate.getUserId());
            }
        }
    }

    private Vacancy findVacancyOrThrowException(long vacancyId){
        return vacancyRepository.findById(vacancyId).orElseThrow(() -> new EntityNotFoundException("Vacancy with id " + vacancyId + " does not exist"));
    }

    public List<VacancyDto> getVacancies(VacancyFilterDto filters) {
        Stream<Vacancy> vacancyStream = vacancyRepository.findAll().stream();
        System.out.println(vacancyFilters.size());
        vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(vacancyStream, filters));
        return vacancyMapper.toDto(vacancyStream.toList());
    }
}
