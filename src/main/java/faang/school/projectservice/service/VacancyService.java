package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mappper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.VacancyFilters.VacancyFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private static final int VACANCY_PLACES = 5;
    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;
    private final VacancyMapper vacancyMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final List<VacancyFilter> filters;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        validateVacancy(vacancyDto.getCreatedBy(), vacancyDto.getProjectId());
        return saveVacancy(vacancyDto);
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        if (vacancyDto.getStatus() == VacancyStatus.CLOSED) {
            Vacancy vacancyToUpdate = getVacancy(vacancyDto.getId());
            if (vacancyToUpdate.getCandidates().size() < VACANCY_PLACES) {
                throw new IllegalArgumentException("There are not enough candidates for this vacancy to close");
            }
        }
        validateVacancy(vacancyDto.getUpdatedBy(), vacancyDto.getProjectId());

        return saveVacancy(vacancyDto);
    }

    public void deleteVacancy(long id) {
        Vacancy vacancy = getVacancy(id);
        vacancy.getCandidates().stream()
                .flatMap(candidate -> teamMemberJpaRepository.findByUserId(candidate.getUserId()).stream())
                .forEach(teamMember -> {
                    if (teamMember.getRoles().contains(TeamRole.INTERN)) {
                        teamMemberJpaRepository.delete(teamMember);
                    }
                });
        vacancyRepository.deleteById(id);
    }

    public List<VacancyDto> getVacancies(VacancyFilterDto filter) {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        if (vacancies.isEmpty()) {
            return new ArrayList<>();
        }

        filters.stream()
                .filter(f -> f.isApplicable(filter))
                .forEach(f -> f.apply(vacancies.stream(), filter));
        return vacancies.stream().map(vacancyMapper::toDto).toList();
    }

    private void validateVacancy(Long updaterId, Long projectId) {
        TeamMember vacancyUpdater = teamMemberRepository.findById(updaterId);
        List<TeamRole> updaterRoles = vacancyUpdater.getRoles();

        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException("There is no project with this id");
        } else if (!updaterRoles.contains(TeamRole.OWNER) && !updaterRoles.contains(TeamRole.MANAGER)) {
            throw new DataValidationException("The vacancy creator doesn't have the required role");
        }
    }

    private VacancyDto saveVacancy(VacancyDto vacancyDto) {
        Vacancy vacancyToSave = vacancyMapper.toModel(vacancyDto);
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
        vacancyToSave.setProject(project);
        Vacancy savedVacancy = vacancyRepository.save(vacancyToSave);
        return vacancyMapper.toDto(savedVacancy);
    }

    private Vacancy getVacancy(long id) {
        return vacancyRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't found vacancy with this id"));
    }
}
