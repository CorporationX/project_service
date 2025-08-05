package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Маппер для преобразования между сущностью {@link faang.school.projectservice.model.Vacancy}
 * и DTO объектами: {@link faang.school.projectservice.dto.vacancy.VacancyCreateDto},
 * {@link faang.school.projectservice.dto.vacancy.VacancyUpdateDto},
 * {@link faang.school.projectservice.dto.vacancy.VacancyDto}.
 * <p>
 * Используется библиотека MapStruct с интеграцией в Spring-контекст.
 * Игнорирует несопоставленные поля.
 * </p>
 *
 * @author Myrza
 * @since 20.07.2025
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = CandidateMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    /**
     * Преобразует {@link VacancyCreateDto} в сущность {@link faang.school.projectservice.model.Vacancy}.
     * Устанавливает статус вакансии по умолчанию как {@code OPEN}.
     *
     * @param createDto входной DTO
     * @return новая сущность вакансии
     */
    @Mapping(target = "status", constant = "OPEN")
    Vacancy toEntity(VacancyCreateDto createDto);

    /**
     * Обновляет существующую сущность {@link faang.school.projectservice.model.Vacancy}
     * на основе данных из {@link VacancyUpdateDto}.
     *
     * @param dto    DTO с обновлёнными данными
     * @param entity сущность, в которую вносятся изменения
     */
    void update(VacancyUpdateDto dto, @MappingTarget Vacancy entity);

    /**
     * Преобразует сущность {@link faang.school.projectservice.model.Vacancy}
     * в DTO {@link VacancyDto} для отображения.
     *
     * @param entity сущность вакансии
     * @return представление вакансии в виде DTO
     */
    @Mapping(target = "projectId", source = "project.id")
    VacancyDto toViewDto(Vacancy entity);
}
