package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.model.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidateMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "resumeDocKey", ignore = true)
    Candidate toCandidateEntity(CandidateDto dto);

    CandidateDto toCandidateDto(Candidate candidate);

    List<Candidate> toCandidateEntityList(List<CandidateDto> dto);
    List<CandidateDto> toCandidateDtoList(List<Candidate> entities);
}
