package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(target = "project", ignore = true)
    Vacancy toEntity(VacancyDto vacancyDto);

    @Mapping(source = "project", target = "projectId", qualifiedByName = "projectMapProjectId")
    VacancyDto toDto(Vacancy vacancy);

    @Named("projectMapProjectId")
    private Long projectMapProjectId(Project project) {
        return project.getId();
    }
}
