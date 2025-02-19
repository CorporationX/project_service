package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyCoverDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    Vacancy toEntity(VacancyCoverDto dto);

    VacancyCoverDto toCoverDto(Vacancy entity);

    void update(@MappingTarget Vacancy entity, VacancyCoverDto dto);
}
