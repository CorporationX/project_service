package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static faang.school.projectservice.model.CandidateStatus.ACCEPTED;
import static faang.school.projectservice.model.TeamRole.OWNER;
import static faang.school.projectservice.model.VacancyStatus.CLOSED;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    public VacancyDto createVacancy(VacancyDto vacancyDto, Long createdBy) {
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
        teamMemberRepository.findById(createdBy).setRoles(Collections.singletonList(OWNER));
        Vacancy vacancy = Vacancy.builder()
                .name(vacancyDto.getName())
                .createdBy(createdBy)
                .description("junior-dev")
                .status(VacancyStatus.OPEN)
                .count(5)
                .build();
        project.addVacancy(vacancy);
        projectRepository.save(project);
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    public void updateVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = getVacancy(vacancyDto.getId());
        if (vacancy.getCandidates().size() < vacancy.getCount()) {
            System.out.println("Нужно больше кандидатов");
        }
        vacancy.getCandidates()
                .forEach(candidate -> candidate.setCandidateStatus(ACCEPTED));
        vacancy.setStatus(CLOSED);
    }

    public void deleteVacancy(Long id) {
        getVacancy(id)
                .getCandidates()
                .forEach(candidate -> {
                    if (!candidate.getCandidateStatus().equals(ACCEPTED)) {
                        vacancyRepository.deleteById(candidate.getUserId());
                    }
                });
    }

    public List<VacancyDto> getVacanciesWithFilters(String name, String position) {
        List<Vacancy> vacancyWithFilter = vacancyRepository.findAll();
        if (name != null) vacancyWithFilter = filterName(vacancyWithFilter, name);
        if (position != null) vacancyWithFilter = filterDescription(vacancyWithFilter, position);
        return vacancyWithFilter.stream()
                .map(vacancyMapper::toDto)
                .toList();
    }

    public Vacancy getVacancy(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Такой вакансии нет"));
    }

    private List<Vacancy> filterName(List<Vacancy> vacancyWithFilter, String name) {
        return vacancyWithFilter.stream()
                .filter(vacancy -> vacancy.getName().contains(name))
                .toList();
    }

    private List<Vacancy> filterDescription(List<Vacancy> vacancyWithFilter, String position) {
        return vacancyWithFilter.stream()
                .filter(vacancy -> vacancy.getDescription().contains(position))
                .toList();
    }
}

