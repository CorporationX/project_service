package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    VacancyDto toDto(Vacancy vacancy);

    Vacancy toEntity(VacancyDto vacancyDto);

    void updateVacancyFromDto(VacancyDto vacancyDto, @MappingTarget Vacancy vacancyToUpdate);
}
