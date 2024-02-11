package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface VacancyMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "count", target = "candidatesCount")
    @Mapping(source = "candidates", target = "candidatesIds", qualifiedByName = "mapCandidates")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(target = "candidates", ignore = true)
    @Mapping(target = "project.id", ignore = true)
    Vacancy toEntity(VacancyDto dto);

    @Named("mapCandidates")
    default List<Long> mapCandidates(List<Candidate> candidates) {
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }
}
