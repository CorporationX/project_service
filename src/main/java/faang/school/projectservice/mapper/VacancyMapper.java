package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mapping(target = "project", ignore = true)
    @Mapping(source = "tutorId", target = "createdBy")
    Vacancy toEntity(VacancyDto vacancyDto);
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "createdBy", target = "tutorId")
    VacancyDto toDto(Vacancy vacancy);

    List<VacancyDto> toDto(List<Vacancy> vacancies);
}
