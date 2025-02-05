package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.model.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CandidateMapper {

    @Mapping(target = "vacancy.id", source = "vacancyId")
    Candidate toEntity(CandidateDto dto);

    @Mapping(target = "vacancyId", source = "vacancy.id")
    CandidateDto toDto(Candidate entity);

    List<Candidate> toEntityList(List<CandidateDto> dtoList);

}
