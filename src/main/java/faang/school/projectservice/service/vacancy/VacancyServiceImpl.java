package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.candidate.CandidateService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class VacancyServiceImpl implements VacancyService {
    private static final int MIN_CANDIDATES_QUALITY = 5;
    private final VacancyRepository vacancyRepository;
    private final CandidateService candidateServiceImpl;
    private final ProjectRepository projectRepository;
    private final TeamMemberService teamMemberServiceImpl;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final List<VacancyFilter> vacancyFilters;

    @Override
    public VacancyDto create(VacancyDto vacancyDto) {
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
        vacancyValidator.checkRolesOfVacancyCreator(vacancyDto);
        Vacancy newVacancy = vacancyMapper.toEntity(vacancyDto);
        newVacancy.setProject(project);
        return vacancyMapper.toDto(vacancyRepository.save(newVacancy));
    }

    @Override
    public VacancyDto update(VacancyDto vacancyDto) {
        Vacancy updatingVacancy = findVacancyById(vacancyDto.getId());
        vacancyValidator.checkRolesOfVacancyUpdater(vacancyDto);
        vacancyValidator.checkCandidatesNumbers(vacancyDto, MIN_CANDIDATES_QUALITY);
        updatingVacancy.setUpdatedAt(LocalDateTime.now());
        vacancyRepository.save(updatingVacancy);
        return vacancyDto;
    }

    @Override
    public boolean delete(Long id) {
        VacancyDto vacancy = findById(id);
        deleteCandidates(vacancy.getCandidateIds(), vacancy.getProjectId());
        vacancyRepository.deleteById(id);
        return true;
    }

    /**
     * Метод удаляет всех членов команды с ролью TeamRole.Intern,
     * если их CandidateStatus не является CandidateStatus.ACCEPTED
     * @param vacancyIds список с id ваканий
     * @param projectId id проекта, из которого удаляются члены команды с ролью TeamRole.Intern
     */
    private void deleteCandidates(List<Long> vacancyIds, Long projectId) {
        vacancyIds.forEach(userId -> {
            Candidate candidate = candidateServiceImpl.findById(userId);
            if (candidate.getCandidateStatus() != CandidateStatus.ACCEPTED) {
                TeamMember teamMember = teamMemberServiceImpl.findByUserIdAndProjectId(userId, projectId);
                boolean isIntern = teamMember.getRoles().stream()
                        .anyMatch(role -> role.equals(TeamRole.INTERN));
                if (isIntern) {
                    teamMemberServiceImpl.deleteById(userId);
                }
            }
        });
    }

    @Override
    public VacancyDto findById(Long id) {
        return vacancyMapper.toDto(findVacancyById(id));
    }

    private Vacancy findVacancyById(Long id) {
        return vacancyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("vacancy with id = %d not found"));
    }

    private List<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }

    @Override
    public List<VacancyDto> findAllDto() {
        return vacancyMapper.toDtoList(findAll());
    }

    @Override
    public List<VacancyDto> findAllWithFilter(VacancyFilterDto vacancyFilterDto) {
        List<VacancyDto> allVacancies = findAllDto();
        Stream<VacancyDto> streamVacancies = allVacancies.stream();
        List<VacancyFilter> suitableFilters = vacancyFilters.stream()
                .filter(vacancyFilter -> vacancyFilter.isApplicable(vacancyFilterDto))
                .toList();
        for (VacancyFilter vacancyFilter : suitableFilters) {
            streamVacancies = vacancyFilter.filter(streamVacancies, vacancyFilterDto);
        }
        return streamVacancies.toList();
    }
}