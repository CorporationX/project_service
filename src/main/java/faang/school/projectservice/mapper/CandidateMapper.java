package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.model.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CandidateMapper {

    CandidateDto toCandidateDto(Candidate entity);
}