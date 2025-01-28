package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyCreationRequest;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateRequest;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VacancyMapper {

    VacancyDto toDto(Vacancy vacancy);

    Vacancy toEntity(VacancyCreationRequest request, Project project);

    Vacancy toEntity(VacancyUpdateRequest request, Project project);
}