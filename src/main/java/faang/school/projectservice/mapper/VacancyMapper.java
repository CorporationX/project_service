package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.VacancyDto;
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

    Vacancy toEntity(VacancyDto vacancyDto);

    @Mapping(source = "candidates",target = "candidateIds",qualifiedByName = "toCandidates")
    @Mapping(source = "project.id",target="projectId")
    VacancyDto toDto(Vacancy vacancy);

    @Named("toCandidates")
    default List<Long> mapCandidate(List<Candidate> candidates) {
        List<Long> candidates1 = new ArrayList<>();
        for (Candidate candidate : candidates) {
            candidates1.add(candidate.getId());
        }
        return candidates1;
    }
}
