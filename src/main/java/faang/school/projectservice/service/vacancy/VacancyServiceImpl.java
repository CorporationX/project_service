package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.FilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса управления вакансиями.
 * <p>
 * Обрабатывает создание, обновление, получение списка и отдельной вакансии.
 * Проверяет авторизацию пользователя по роли в проекте (владелец или менеджер).
 * </p>
 *
 * @author Myrza
 * @since 20.07.2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyServiceImpl implements VacancyService {
    private final UserContext userContext;
    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyMapper mapper;
    private final FilterService<Vacancy, VacancyFilterDto> filterService;

    /**
     * Создаёт новую вакансию в проекте.
     * Проверяет наличие проекта и права пользователя (владелец или менеджер).
     *
     * @param createDto DTO с параметрами новой вакансии
     * @return созданная вакансия {@link VacancyDto}
     * @throws EntityNotFoundException если проект не найден
     * @throws ForbiddenException      если у пользователя нет прав на создание вакансии
     */
    @Override
    public VacancyDto create(VacancyCreateDto createDto) {
        var userId = userContext.getUserId();
        var project = projectRepository.findById(createDto.projectId())
                .orElseThrow(() -> {
                    log.warn("Нет проекта с таким projectId {}", createDto.projectId());
                    return new EntityNotFoundException("Нет проекта с таким projectId " + createDto.projectId());
                });
        var isUserManager = teamMemberRepository.isUserHasRole(userId, project.getId(), TeamRole.MANAGER);
        if (project.getOwnerId() != userId && !isUserManager) {
            throw new ForbiddenException("У пользователя нет доступа для создания вакансии в этом проекте");
        }
        var vacancy = mapper.toEntity(createDto);
        vacancy.setCreatedBy(userId);
        vacancy.setProject(project);
        vacancy = vacancyRepository.save(vacancy);
        return mapper.toViewDto(vacancy);
    }

    /**
     * Обновляет существующую вакансию по её идентификатору.
     * Проверяет права доступа: пользователь должен быть владельцем проекта или менеджером.
     *
     * @param vacancyId ID вакансии
     * @param updateDto DTO с обновлёнными полями
     * @return обновлённая вакансия {@link VacancyDto}
     * @throws EntityNotFoundException если вакансия не найдена
     * @throws ForbiddenException      если у пользователя нет доступа
     */
    @Override
    public VacancyDto update(Long vacancyId, VacancyUpdateDto updateDto) {
        var userId = userContext.getUserId();
        var vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> {
                    log.warn("Нет вакансии с таким id {}", vacancyId);
                    return new EntityNotFoundException("Нет вакансии с таким id " + vacancyId);
                });
        var isUserManager = teamMemberRepository.isUserHasRole(userId, vacancy.getProject().getId(), TeamRole.MANAGER);
        if (vacancy.getProject().getOwnerId() != userId && !isUserManager) {
            throw new ForbiddenException("У пользователя нет доступа для редактирования вакансии в этом проекте");
        }
        mapper.update(updateDto, vacancy);
        vacancy.setUpdatedBy(userId);
        vacancy = vacancyRepository.save(vacancy);
        return mapper.toViewDto(vacancy);
    }

    /**
     * Возвращает отфильтрованный список вакансий по переданным критериям.
     *
     * @param filterDto DTO с параметрами фильтрации
     * @return список подходящих вакансий {@link VacancyDto}
     */
    @Override
    public List<VacancyDto> getList(VacancyFilterDto filterDto) {
        filterDto.validate();
        var vacancies = vacancyRepository.findAll();
        vacancies = filterService.getFilteredList(vacancies, filterDto);
        return vacancies.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    /**
     * Возвращает вакансию по её идентификатору.
     *
     * @param vacancyId ID вакансии
     * @return вакансия {@link VacancyDto}
     * @throws EntityNotFoundException если вакансия не найдена
     */
    @Override
    public VacancyDto getById(Long vacancyId) {
        var vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> {
                    log.warn("Вакансия с таким id {} не найдена", vacancyId);
                    return new EntityNotFoundException("Вакансия с таким id " + vacancyId + " не найдена");
                });
        return mapper.toViewDto(vacancy);
    }
}
