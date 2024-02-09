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
import java.util.Map;

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

    public Map<Long, VacancyDto> getVacanciesWithFilters(Long projectId, Long position, String name) {
        vacancyRepository.findAll();
        return null;
    }

    public Vacancy getVacancy(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Такой вакансии нет"));
    }
}

