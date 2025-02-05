package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(target = "project.id", source = "projectId")
    Vacancy toEntity(VacancyUpdateDto vacancyUpdateDto);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "candidatesId", source = "candidates", qualifiedByName = "mapCandidatesToCandidatesIds")
    VacancyResponseDto toDto(Vacancy vacancy);

    List<VacancyResponseDto> toDtoList(List<Vacancy> vacancies);

    @Named("mapCandidatesToCandidatesIds")
    default List<Long> mapCandidatesToCandidatesIds(List<Candidate> candidates) {
        if (candidates == null) {
            return List.of();
        }
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }
}
