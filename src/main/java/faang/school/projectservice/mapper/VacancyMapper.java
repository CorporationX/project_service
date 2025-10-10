package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VacancyMapper {

    @Mapping(target = "project", ignore = true)
    Vacancy toVacancy(CreateVacancyDto createVacancyDto);

    @Mapping(target = "projectId", source = "project.id")
    VacancyDto toVacancyDto(Vacancy vacancy);

    static void update(Vacancy vacancy, UpdateVacancyDto updateVacancyDto) {
        VacancyStatus vacancyStatus = updateVacancyDto.vacancyStatus();
        if (vacancyStatus != null) {
            vacancy.setStatus(vacancyStatus);
        }

        String name = updateVacancyDto.name();
        if (name != null) {
            vacancy.setName(name);
        }

        String description = updateVacancyDto.description();
        if (description != null) {
            vacancy.setName(description);
        }
    }
}
