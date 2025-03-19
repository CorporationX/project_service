package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyCandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.mapper.VacancyCandidateMapper;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.VacancyStatus.CLOSED;

@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyService {
    private final VacancyRepository repository;
    private final UserContext userContext;
    private final VacancyMapper mapper;
    private final VacancyCandidateMapper candidateMapper;
    private final List<VacancyFilter> vacancyFilters = new ArrayList<>();
    private final TeamMemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    public VacancyCandidateDto createVacancy(VacancyDto vacancyDto) {
        validateCreatorRole();
        Vacancy vacancy  = mapper.toEntity(vacancyDto);
        Long projectId = vacancyDto.getProjectId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new DataValidationException("Проект с id: {} не найден.", projectId));
        mapper.updateVacancyFromDto(vacancyDto, vacancy);
        vacancy.setProject(project);
        vacancy.setStatus(VacancyStatus.OPEN);
        validateVacancyData(vacancy);
        updateMetaData(vacancy);
        Vacancy savedVacancy = repository.save(vacancy);
        log.info("Вакансия {} успешно создана.", savedVacancy.getName());

        return candidateMapper.toDto(savedVacancy);
    }

    public VacancyCandidateDto updateVacancy(long vacancyId, VacancyDto vacancyDto) {
        validateCreatorRole();

        if (vacancyDto == null) {
            throw new DataValidationException("Вакансия не может быть пустой");
        }

        Vacancy targetVacancy = repository.findById(vacancyId)
                .orElseThrow(() -> new DataValidationException("Вакансия c id: {} не найдена", vacancyId));
        mapper.updateVacancyFromDto(vacancyDto, targetVacancy);

        if (vacancyDto.getCandidates() != null
                && !vacancyDto.getCandidates().equals(targetVacancy.getCandidates())) {
            validateAndAddNewCandidates(targetVacancy, vacancyDto.getCandidates());
        }
        validateVacancyData(targetVacancy);
        validateIsVacancyClosable(vacancyDto);

        updateMetaData(targetVacancy);
        repository.save(targetVacancy);
        log.info("Вакансия {} обновлена", targetVacancy.getName());
        return candidateMapper.toDto(targetVacancy);
    }

    public List<VacancyCandidateDto> findVacancy(VacancyFilterDto filter) {
        List<Vacancy> vacancyList = repository.findAll();
        Stream<Vacancy> vacancyStream = vacancyList.stream();
        return filterVacancies(vacancyStream, filter)
                .map(candidateMapper::toDto)
                .toList();
    }

    private Stream<Vacancy> filterVacancies(Stream<Vacancy> vacancies, VacancyFilterDto filters) {
        List<VacancyFilter> applicableFilters =
                vacancyFilters.stream()
                        .filter(filter -> filter.isApplicable(filters))
                        .toList();
        for (VacancyFilter vacancyFilter : applicableFilters) {
            vacancies = vacancyFilter.apply(vacancies, filters);
        }
        return vacancies;
    }

    public VacancyCandidateDto getVacancyInfoById(long vacancyId) {
        Vacancy vacancy = repository.findById(vacancyId)
                .orElseThrow(() -> new DataValidationException("Вакансия не найдена"));
        return candidateMapper.toDto(vacancy);
    }

    private void validateCreatorRole() {
        TeamMember creator = memberRepository.findById(
                userContext.getUserId()).orElseThrow(() ->
                new DataValidationException("Пользователь не найден."));
        if (creator.getRoles().stream().noneMatch(role -> role == TeamRole.OWNER || role == TeamRole.MANAGER)) {
            throw new DataValidationException("Вы не имеете прав на публикацию вакансий.");
        }
    }

    private void validateAndAddNewCandidates(Vacancy vacancy, List<Candidate> candidates) {
        candidates.stream()
                .filter(candidate -> !vacancy.getCandidates().contains(candidate))
                .forEach(candidate -> addAndValidateCandidate(vacancy, candidate));
    }

    private void addAndValidateCandidate(Vacancy vacancy, Candidate candidate) {
        validateUserHasNoRoles(candidate.getUserId());
        vacancy.getCandidates().add(candidate);
    }

    private void validateUserHasNoRoles(Long userId) {
        TeamMember participant = memberRepository.findById(userId).orElseThrow(() ->
                new DataValidationException("Пользователь не найден"));
        List<TeamRole> participantRoles = participant.getRoles();
        if (!participantRoles.isEmpty()) {
            throw new DataValidationException("Пользователь состоит в компании");
        }
    }

    private boolean hiredCandidates(VacancyDto vacancyDto) {
        List<Candidate> candidatesList = vacancyDto.getCandidates();
        int actualCount = candidatesList.size();
        int requiredCount = vacancyDto.getCount();
        if (actualCount != requiredCount) {
            throw new DataValidationException("Достаточное количество кандидатов не набрано");
        }
        return true;
    }

    private void validateIsVacancyClosable(VacancyDto vacancyDto) {
        if (vacancyDto.getStatus() == CLOSED && !hiredCandidates(vacancyDto)) {
            throw new DataValidationException(
                    "Нельзя закрыть вакансию пока не набрано достаточное количество кандидатов");
        }
    }

    private void updateMetaData(Vacancy vacancy) {
        vacancy.setUpdatedAt(LocalDateTime.now());
        vacancy.setUpdatedBy(userContext.getUserId());
    }

    private void validateVacancyData(Vacancy vacancy) {
        if (vacancy.getName() == null || vacancy.getName().isBlank()) {
            throw  new DataValidationException("Имя не может быть пустым");
        }
        if (vacancy.getSalary() <= 0) {
            throw new DataValidationException("Заработная плата должна быть больше 0");
        }
        if (vacancy.getCount() <= 0) {
            throw new DataValidationException("Количество вакантных мест не может быть меньше 1");
        }
    }

}
