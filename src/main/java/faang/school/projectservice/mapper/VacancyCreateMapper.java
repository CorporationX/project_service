package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.VacancyCreateDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyCreateMapper {
    VacancyCreateDto toVacancyDto(Vacancy vacancy);
    Vacancy toVacancy(VacancyCreateDto vacancyCreateDto);
}
