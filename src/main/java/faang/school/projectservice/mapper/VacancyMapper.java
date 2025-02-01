package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.VacancyDTO;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "candidates", target = "candidateIds", qualifiedByName = "mapCandidatesToId")
    VacancyDTO toDto(Vacancy vacancy);

    Vacancy toEntity(VacancyDTO dto);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void update(VacancyDTO vacancyDTO, @MappingTarget Vacancy vacancy);

    List<VacancyDTO> toDtoList(List<Vacancy> vacancies);

    @Named("mapCandidatesToId")
    default List<Long> mapCandidatesToId(List<Candidate> candidates) {
        if(candidates != null) {
            return candidates.stream().map(Candidate::getId).toList();
        }
        return Collections.emptyList();
    }
}
