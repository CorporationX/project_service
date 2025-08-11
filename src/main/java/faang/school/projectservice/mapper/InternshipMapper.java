package faang.school.projectservice.mapper;

import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.apimodel.InternshipStatusDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Mapper для преобразования между сущностью {@link Internship} и DTO {@link InternshipDto}.
 * <p>
 * Отвечает за маппинг данных между слоями приложения:
 * - Конвертация сущности Internship в DTO для передачи через API.
 * - Конвертация DTO в сущность для сохранения в базе данных.
 * - Преобразование перечислений статусов стажировки между слоями.
 * <p>
 * Также предоставляет вспомогательные методы для работы с датами и удобный метод
 * для создания сущности {@link Internship} с привязкой к объекту {@link Project}.
 * <p>
 * Использует MapStruct для генерации реализации.
 * </p>
 *
 * @author agent
 * @since 04.08.2025
 */
@Mapper(componentModel = "spring")
public interface InternshipMapper {

    /**
     * Преобразует сущность {@link Internship} в DTO {@link InternshipDto}.
     * Игнорирует поля projectId и traineeIds (их нужно заполнять отдельно при необходимости).
     * Выполняет маппинг поля mentorId с вложенным id.
     *
     * @param entity сущность Internship
     * @return DTO InternshipDto
     */
    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "traineeIds", ignore = true)
    @Mapping(target = "mentorId", source = "mentorId.id")
    InternshipDto toDto(Internship entity);

    /**
     * Преобразует DTO {@link InternshipDto} в сущность {@link Internship}.
     * Игнорирует поля project, interns и schedule (их нужно заполнять отдельно).
     * Выполняет маппинг поля mentorId.id из mentorId.
     *
     * @param dto DTO InternshipDto
     * @return сущность Internship
     */
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "interns", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "mentorId.id", source = "mentorId")
    Internship toEntity(InternshipDto dto);

    /**
     * Преобразует статус из DTO {@link InternshipStatusDto} в сущность {@link InternshipStatus}.
     *
     * @param dtoStatus статус из DTO
     * @return статус сущности
     */
    InternshipStatus map(InternshipStatusDto dtoStatus);

    /**
     * Преобразует статус из сущности {@link InternshipStatus} в DTO {@link InternshipStatusDto}.
     *
     * @param entityStatus статус сущности
     * @return статус DTO
     */
    InternshipStatusDto map(InternshipStatus entityStatus);

    /**
     * Удобный метод для преобразования DTO в сущность с явной установкой связанного проекта.
     *
     * @param dto     DTO InternshipDto
     * @param project объект проекта {@link Project}, который будет установлен в сущность
     * @return сущность Internship с установленным проектом
     */
    default Internship toEntityWithProject(InternshipDto dto, Project project) {
        Internship internship = toEntity(dto);
        internship.setProject(project);
        return internship;
    }

    /**
     * Преобразует {@link LocalDateTime} в {@link OffsetDateTime} с зоной UTC.
     *
     * @param localDateTime локальное время
     * @return время со смещением UTC или null, если входящее значение null
     */
    default OffsetDateTime asOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }

    /**
     * Преобразует {@link OffsetDateTime} в {@link LocalDateTime}.
     *
     * @param offsetDateTime время со смещением
     * @return локальное время или null, если входящее значение null
     */
    default LocalDateTime asLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }
}

