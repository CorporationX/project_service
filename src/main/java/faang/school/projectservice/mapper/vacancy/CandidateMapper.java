package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.CandidateCreateDto;
import faang.school.projectservice.dto.vacancy.CandidateDto;
import faang.school.projectservice.model.Candidate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface CandidateMapper {

    default Candidate toCandidate(CandidateCreateDto candidateCreateDto) {
        if (candidateCreateDto == null) {
            return null;
        }
        return Candidate.builder()
                .userId(candidateCreateDto.userId())
                .username(candidateCreateDto.username())
                .resumeDocKey(candidateCreateDto.resumeDocKey())
                .coverLetter(candidateCreateDto.coverLetter())
                .build();
    }


    CandidateDto toCandidateDto(Candidate candidate);
}
