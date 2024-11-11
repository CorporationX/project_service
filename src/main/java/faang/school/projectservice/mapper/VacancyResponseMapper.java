package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.VacancyResponseDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyResponseMapper {
    VacancyResponseDto toVacancyDto(Vacancy vacancy);
    Vacancy toVacancy(VacancyResponseDto vacancyUpdateDto);
}
