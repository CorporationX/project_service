package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface VacancyMapper {

    @Mapping(source = "candidates", target = "candidatesId", qualifiedByName = "mapCandidatesId")
    @Mapping(source = "project.id", target = "projectId")
    VacancyDto toVacancyDto(Vacancy vacancy);

    @Mapping(target = "candidates", ignore = true)
    @Mapping(source = "candidatesId", target = "candidates.id")
    @Mapping(source = "projectId", target = "project.id")
    Vacancy toVacancy(VacancyDto vacancyDto);

    @Named("mapCandidatesId")
    default List<Long> mapCandidatesId(List<Candidate> candidates) {
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }
}