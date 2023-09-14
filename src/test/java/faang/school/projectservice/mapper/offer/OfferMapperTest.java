package faang.school.projectservice.mapper.offer;

import faang.school.projectservice.dto.offer.OfferDto;
import faang.school.projectservice.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OfferMapperTest {
    @Spy
    private OfferMapperImpl mapper;
    private Offer offer;
    private OfferDto offerDto;

    @BeforeEach
    void setUp() {
        offer = Offer.builder().id(1L).vacancy(Vacancy.builder().id(1L).build())
                .candidate(Candidate.builder().id(1L).build())
                .createdBy(0L)
                .team(Team.builder().id(1L).build()).build();

        offerDto = OfferDto.builder().id(1L).vacancyId(1L).candidateId(1L).createdBy(0L).teamId(1L).build();
    }

    @Test
    void toDto_Test() {
        assertEquals(mapper.toDto(offer), offerDto);
    }

    @Test
    void toEntity_Test() {
        assertEquals(mapper.toEntity(offerDto), offer);
    }

    @Test
    void idToVacancy_Test() {
        assertEquals(mapper.idToVacancy(1L), Vacancy.builder().id(1L).build());
    }

    @Test
    void idToCandidate_Test() {
        assertEquals(mapper.idToCandidate(1L), Candidate.builder().id(1L).build());
    }

    @Test
    void idToTeam_Test() {
        assertEquals(mapper.idToTeam(1L), Team.builder().id(1L).build());
    }
}