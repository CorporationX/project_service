package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CandidateDto;
import faang.school.projectservice.dto.CreateCandidateDto;
import faang.school.projectservice.model.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CandidateMapper {
    Candidate toEntity(CreateCandidateDto dto);

    @Mapping(target = "vacancyId", source = "vacancy.id")
    @Mapping(target = "teamId", source = "team.id")
    CandidateDto toDto(Candidate candidate);
}
