package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "candidates", ignore = true)
    Vacancy toEntity(VacancyDto vacancyDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "candidates", target = "candidatesIds", qualifiedByName = "candidatesMapToCandidatesIds")
    VacancyDto toDto(Vacancy vacancy);

    @Named("candidatesMapToCandidatesIds")
    default List<Long> candidatesMapToCandidatesId(List<Candidate> candidates) {
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }
}
