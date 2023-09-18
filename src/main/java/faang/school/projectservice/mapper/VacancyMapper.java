package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(target = "projectId", source = "project.id")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(target = "project.id", source = "projectId")
    Vacancy toEntity(VacancyDto vacancy);
}
