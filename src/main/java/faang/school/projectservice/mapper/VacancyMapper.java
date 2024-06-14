package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.ProjectService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface VacancyMapper {
    @Mapping(source = "projectId", target = "project")
    Vacancy toEntity(VacancyDto vacancyDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "candidates", target = "candidateIds", qualifiedByName = "candidatesToIds")
    VacancyDto toDto(Vacancy vacancy);

    List<VacancyDto> toDtoList(List<Vacancy> vacancies);

    @Mapping(source = "projectId", target = "project")
    void update(VacancyDto vacancyDto, @MappingTarget Vacancy vacancy);

    @Named("candidatesToIds")
    default List<Long> candidatesToIds(List<Candidate> candidates) {
        List<Long> resultList = new ArrayList<>();
        if (candidates != null) {
            resultList = candidates.stream().map(Candidate::getId).toList();
        }
        return resultList;
    }
    Project map(Long value);
}
