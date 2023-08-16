package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDtoGetReq;
import faang.school.projectservice.dto.vacancy.VacancyDtoUpdateReq;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mapping(source = "id", target = "vacancyId")
    @Mapping(source = "project.id", target = "projectId")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(source = "id", target = "vacancyId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "candidates", target = "candidatesId", qualifiedByName = "getListIdsCandidates")
    VacancyDtoGetReq toDtoGetReq(Vacancy vacancy);

    @Mapping(target = "id", ignore = true)
    Vacancy toEntity(VacancyDto vacancyDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(VacancyDtoUpdateReq vacancyDto, @MappingTarget Vacancy vacancy);

    @Named(value = "getListIdsCandidates")
    default List<Long> getListIdsCandidates(List<Candidate> candidates) {
        if (candidates == null) {
            return new ArrayList<>();
        }
        return candidates.stream().map(Candidate::getId).toList();
    }
}
