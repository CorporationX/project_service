package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * @author Alexander Bulgakov
 */
@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface VacancyMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "count", target = "candidatesCount")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(target = "candidates", ignore = true)
    @Mapping(target = "project.id", ignore = true)
    Vacancy toEntity(VacancyDto dto);
}
