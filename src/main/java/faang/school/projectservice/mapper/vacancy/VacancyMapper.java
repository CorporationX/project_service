package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.Vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.Vacancy.ExtendedVacancyDto;
import faang.school.projectservice.dto.Vacancy.UpdateVacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    VacancyMapper INSTANCE = Mappers.getMapper(VacancyMapper.class);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "candidateIds", source = "candidates", qualifiedByName = "toCandidateIds")
    ExtendedVacancyDto toDto(Vacancy vacancy);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "candidates", ignore = true)
    Vacancy toEntity(CreateVacancyDto vacancyDto);

    @Mapping(target = "candidates", source = "candidateIds", qualifiedByName = "toCandidateEntities")
    @Mapping(target = "project", ignore = true)
    void updateFromDto(UpdateVacancyDto vacancyDto, @MappingTarget Vacancy vacancy);

    @Named("toCandidateEntities")
    default List<Candidate> toCandidateEntities(List<Long> ids) {
        return ids.stream().map(id -> {
            Candidate c = new Candidate();
            c.setId(id);
            return c;
        }).toList();
    }

    List<ExtendedVacancyDto> entityListToDtoList(List<Vacancy> vacancies);

    @Named("toCandidateIds")
    default List<Long> toCandidateIds(List<Candidate> candidates) {
        return candidates.stream().map(Candidate::getId).toList();
    }
}
