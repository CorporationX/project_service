package faang.school.projectservice.validator.offer;

import faang.school.projectservice.dto.offer.OfferDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Offer;
import faang.school.projectservice.model.Team;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class OfferValidatorTest {
    @InjectMocks
    private OfferValidator offerValidator;

    @Test
    void defaultOfferServiceValidation_Test() {
        OfferDto offerDto = OfferDto.builder().vacancyId(1L).candidateId(1L).teamId(1L).createdBy(1L).build();

        Exception exception = Assertions.assertThrows(DataValidationException.class,
                () -> offerValidator.defaultOfferServiceValidation(offerDto, 2L));

        Assertions.assertEquals(exception.getMessage(), "You cant change not yours offer");
    }

    @Test
    void createOfferServiceValidation_Test() {
        Offer offer = Offer.builder().team(Team.builder().teamMembers(List.of()).build()).build();

        Exception exception = Assertions.assertThrows(DataValidationException.class,
                () -> offerValidator.createOfferServiceValidation(offer, 2L));

        Assertions.assertEquals(exception.getMessage(), "You are not a part of the team");
    }
}