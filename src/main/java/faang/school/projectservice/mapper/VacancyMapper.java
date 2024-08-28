package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "ownerMember", target = "ownerId")
    VacancyDto toVacancyDto(Vacancy vacancy);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "ownerId", target = "ownerMember")
    Vacancy toVacancy(VacancyDto vacancyDto);

    void updateVacancy(VacancyDto vacancyDto, @MappingTarget Vacancy vacancy);
}