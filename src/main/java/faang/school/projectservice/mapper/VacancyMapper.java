package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VacancyMapper {

    Vacancy toEntity(VacancyDto vacancyDto);

    @Mapping(source = "candidates",target = "candidateIds",qualifiedByName = "toCandidates")
    @Mapping(source = "project.id",target="projectId")
    VacancyDto toDto(Vacancy vacancy);

    @Named("toCandidates")
    default List<Long> mapCandidate(List<Candidate> candidates) {
        return candidates.stream().map(Candidate::getId).toList();
    }
    List<VacancyDto> toDtoList(List<Vacancy> vacancies);
}
