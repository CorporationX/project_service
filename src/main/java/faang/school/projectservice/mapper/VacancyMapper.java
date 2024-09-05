package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
public interface VacancyMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "candidates", target = "candidateIds", qualifiedByName = "mapToCandidateIds")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "candidateIds", target = "candidates", qualifiedByName = "mapToCandidateFromIds")
    Vacancy toEntity(VacancyDto vacancyDto);


    @Mapping(source = "candidateIds", target = "candidates", qualifiedByName = "mapToCandidateFromIds")
    void updateVacancy(UpdateVacancyDto updateVacancyDto, @MappingTarget Vacancy vacancy);

    @Named("mapToCandidateIds")
    default List<Long> mapToCandidateIds(List<Candidate> candidates) {
        if (candidates == null) {
            return null;
        }
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }

    @Named("mapToCandidateFromIds")
    default List<Candidate> mapToCandidateFromIds(List<Long> candidateIds) {
        if (candidateIds == null) {
            return null;
        }
        return candidateIds.stream()
                .map(id -> {
                    Candidate candidate = new Candidate();
                    candidate.setId(id);
                    return candidate;
                }).toList();
    }
}
