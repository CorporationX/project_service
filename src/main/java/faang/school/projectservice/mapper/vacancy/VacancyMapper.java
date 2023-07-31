package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mapping(target = "project", source = "projectId", qualifiedByName = "idToProject")
    @Mapping(target = "candidates", source = "candidates", qualifiedByName = "idsToCandidates")
    Vacancy toEntity(VacancyDto vacancyDto);

    @Mapping(target = "projectId", source = "project", qualifiedByName = "projectToId")
    @Mapping(target = "candidates", source = "candidates", qualifiedByName = "CandidatesToIds")
    VacancyDto toDto(Vacancy vacancy);

    @Named("idToProject")
    default Project idToProject(Long id) {
        return Project.builder().id(id).build();
    }

    @Named("idsToCandidates")
    default List<Candidate> idsToCandidates(List<Long> ids) {
        return ids.stream().map(id -> Candidate.builder().id(id).build()).toList();
    }

    @Named("projectToId")
    default Long projectToId(Project project) {
        return project.getId();
    }

    @Named("CandidatesToIds")
    default List<Long> CandidatesToIds(List<Candidate> candidates) {
        return candidates.stream().map(Candidate::getId).toList();
    }
}
