package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VacancyMapper {

    @Mapping(target = "project", ignore = true)
    Vacancy toVacancy(VacancyCreateDto vacancyCreateDto);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "candidatesIds", source = "candidates", qualifiedByName = "mapCandidatesId")
    VacancyDto toVacancyDto(Vacancy vacancy);

    @Named("mapCandidatesId")
    default List<Long> mapCandidatesId(List<Candidate> candidates) {
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }
}

