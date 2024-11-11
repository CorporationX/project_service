package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.VacancyUpdateDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyUpdateMapper {
    VacancyUpdateDto toVacancyDto(Vacancy vacancy);
    Vacancy toVacancy(VacancyUpdateDto vacancyUpdateDto);
}
