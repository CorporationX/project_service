package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "candidates", target = "candidateIds", qualifiedByName = "getCandidateIds")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "candidateIds", target = "candidates", qualifiedByName = "getCandidates")
    Vacancy toEntity(VacancyDto vacancyDto);

    List<VacancyDto> toDtoList(List<Vacancy> vacancies);

    @Named("getCandidateIds")
    default List<Long> getCandidateIds(List<Candidate> candidates) {
        if (candidates == null) {
            return new ArrayList<>();
        }
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }

    @Named("getCandidates")
    default List<Candidate> getCandidates(List<Long> candidateIds) {
        if (candidateIds == null) {
            return new ArrayList<>();
        }
        return candidateIds.stream()
                .map(id -> {
                    Candidate candidate = new Candidate();
                    candidate.setId(id);
                    return candidate;
                })
                .toList();
    }
}