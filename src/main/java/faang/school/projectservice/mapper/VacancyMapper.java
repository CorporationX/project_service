package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(source = "candidates", target = "candidatesId", qualifiedByName = "mapCandidatesId")
    @Mapping(source = "project.id", target = "projectId")
    VacancyDto toVacancyDto(Vacancy vacancy);

    @Mapping(source = "candidatesId", target = "candidates", qualifiedByName = "mapCandidates")
    @Mapping(source = "projectId", target = "project.id")
    Vacancy toVacancy(VacancyDto vacancyDto);

    @Named("mapCandidatesId")
    default List<Long> mapCandidatesId(List<Candidate> candidates) {
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }

    @Named("mapCandidates")
    default List<Candidate> mapCandidates(List<Long> candidatesId) {
        return candidatesId != null ? candidatesId.stream()
                .map(id -> {
                    Candidate candidate = new Candidate();
                    candidate.setId(id);
                    return candidate;
                })
                .collect(Collectors.toList()) : null;
    }
}
