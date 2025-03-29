package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.CandidateDto;
import faang.school.projectservice.model.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CandidateMapper {
    CandidateDto ToCandidateDto(Candidate candidate);

    List<CandidateDto> ToCandidateDtos(List<Candidate> candidates);
}
