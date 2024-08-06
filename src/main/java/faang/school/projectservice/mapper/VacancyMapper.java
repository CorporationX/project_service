package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    Vacancy toEntity(VacancyDto vacancyDto);

    VacancyDto toDto(Vacancy vacancy);

    List<VacancyDto> toDtoList(List<Vacancy> vacancies);
}
