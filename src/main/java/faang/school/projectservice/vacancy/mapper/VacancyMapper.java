package faang.school.projectservice.vacancy.mapper;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.vacancy.dto.CandidateDto;
import faang.school.projectservice.vacancy.dto.VacancyCreateDto;
import faang.school.projectservice.vacancy.dto.VacancyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    VacancyMapper INSTANCE = Mappers.getMapper(VacancyMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToLong")
    VacancyDto toDto(Vacancy vacancy);
    Vacancy toEntity(VacancyCreateDto dto);

    Candidate toEntity(CandidateDto dto);

    CandidateDto toDto(Candidate candidate);

    @Named("uuidToLong")
    default Long uuidToLong(UUID value) {
        return value == null ? null : value.getMostSignificantBits();
    }


}