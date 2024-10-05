package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.VacancyDtoCreate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapperCreate {
    VacancyDto toVacancyDto(Vacancy vacancy);
    Vacancy vacancyDtoToVacancy(VacancyDtoCreate vacancyDtoCreate);
}
