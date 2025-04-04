package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyRequestDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(uses = { ProjectMapper.class },
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class VacancyMapper {
    private CandidateMapper candidateMapper;

    @Autowired
    public void setCandidateMapper(CandidateMapper candidateMapper) {
        this.candidateMapper = candidateMapper;
    }

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "candidates", ignore = true)
    public abstract Vacancy toVacancyEntity(VacancyRequestDto dto);

    @Mapping(source = "candidates", target = "candidatesDto", qualifiedByName = "mapToCandidatesDto")
    public abstract VacancyDto toVacancyDto(Vacancy entity);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "candidates", ignore = true)
    public abstract void update(VacancyRequestDto dto, @MappingTarget Vacancy entity);

    @Named("mapToCandidatesDto")
    protected List<CandidateDto> mapToCandidatesDto(List<Candidate> candidates) {
        return candidates == null ? null : candidates.stream()
                .map(candidateMapper::toCandidateDto)
                .toList();
    }
}
