package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateDeleteVacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(source = "candidates", target = "candidatesIds", qualifiedByName = "mapCandidatesIds")
    @Mapping(source = "project.id", target = "projectId")
    CreateVacancyDto toCreateDto(Vacancy vacancy);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "candidates", ignore = true)
    Vacancy toEntity(CreateVacancyDto createVacancyDto);

    List<CreateVacancyDto> toCreateDto(List<Vacancy> vacancies);
    List<Vacancy> toEntity(List<CreateVacancyDto> createVacancyDtos);

    @Mapping(source = "candidates", target = "candidatesIds", qualifiedByName = "mapCandidatesIds")
    @Mapping(source = "project.id", target = "projectId")
    UpdateDeleteVacancyDto toUpdateDeleteDto(Vacancy vacancy);
    List<UpdateDeleteVacancyDto> toUpdateDeleteDto(List<Vacancy> vacancies);

    @Named("mapCandidatesIds")
    default List<Long> mapCandidatesIds(List<Candidate> candidates) {
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }
}