package faang.school.projectservice.mapper.offer;

import faang.school.projectservice.dto.offer.OfferDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Offer;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OfferMapper {
    @Mapping(target = "vacancyId", source = "vacancy.id")
    @Mapping(target = "candidateId", source = "candidate.id")
    @Mapping(target = "teamId", source = "team.id")
    OfferDto toDto(Offer offer);

    @Mapping(target = "vacancy", source = "vacancyId", qualifiedByName = "idToVacancy")
    @Mapping(target = "candidate", source = "candidateId", qualifiedByName = "idToCandidate")
    @Mapping(target = "team", source = "teamId", qualifiedByName = "idToTeam")
    Offer toEntity(OfferDto offerDto);

    @Named("idToVacancy")
    default Vacancy idToVacancy(Long id) {
        return Vacancy.builder().id(id).build();
    }

    @Named("idToCandidate")
    default Candidate idToCandidate(Long id) {
        return Candidate.builder().id(id).build();
    }

    @Named("idToTeam")
    default Team idToTeam(Long id) {
        return Team.builder().id(id).build();
    }
}
