package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CandidatesMapperHelper.class)
public interface VacancyMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "candidates", target = "candidatesIds", qualifiedByName = "candidateToId")
    @Mapping(source = "createdBy", target = "curatorId")
    @Mapping(source = "count", target = "workersRequired")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "candidatesIds", target = "candidates", qualifiedByName = "idToCandidate")
    @Mapping(source = "curatorId", target = "createdBy")
    @Mapping(source = "workersRequired", target = "count")
    Vacancy toEntity(VacancyDto vacancyDto);

    @Named("candidateToId")
    default List<Long> candidatesToIds(List<Candidate> candidates) {
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }

    @Named("idToCandidate")
    default List<Candidate> idsToCandidates(List<Long> candidatesIds, @Context CandidatesMapperHelper helper) {
        return helper.idsToCandidates(candidatesIds);
    }
}
