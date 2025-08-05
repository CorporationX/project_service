package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;

import java.util.List;

/**
 * /**
 * Сервисный интерфейс для работы с вакансиями.
 * <p>
 * Определяет операции создания, обновления, получения списка и получения вакансии по идентификатору.
 *
 * @author Myrza
 * @since 20.07.2025
 */
public interface VacancyService {
    /**
     * Создаёт новую вакансию на основе переданных данных.
     *
     * @param createDto DTO с параметрами новой вакансии.
     * @return Созданная вакансия в виде {@link VacancyDto}.
     */
    VacancyDto create(VacancyCreateDto createDto);

    /**
     * Обновляет существующую вакансию.
     *
     * @param vacancyId Идентификатор вакансии, которую необходимо обновить.
     * @param updateDto DTO с новыми значениями полей.
     * @return Обновлённая вакансия в виде {@link VacancyDto}.
     */
    VacancyDto update(Long vacancyId, VacancyUpdateDto updateDto);

    /**
     * Возвращает список вакансий, удовлетворяющих условиям фильтрации.
     *
     * @param filterDto DTO с параметрами фильтрации.
     * @return Список подходящих вакансий.
     */
    List<VacancyDto> getList(VacancyFilterDto filterDto);

    /**
     * Возвращает вакансию по её идентификатору.
     *
     * @param vacancyId Идентификатор вакансии.
     * @return Найденная вакансия в виде {@link VacancyDto}.
     */
    VacancyDto getById(Long vacancyId);
}
