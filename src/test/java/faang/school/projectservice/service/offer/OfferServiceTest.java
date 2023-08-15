package faang.school.projectservice.service.offer;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.offer.OfferDto;
import faang.school.projectservice.mapper.offer.OfferMapperImpl;
import faang.school.projectservice.repository.OfferRepository;
import faang.school.projectservice.validator.offer.OfferValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {
    @Mock
    private UserContext userContext;
    @Mock
    private OfferValidator offerValidator;
    @Spy
    private OfferMapperImpl offerMapper;
    @Mock
    private OfferRepository offerRepository;
    @InjectMocks
    private OfferService service;

    OfferDto offerDto = OfferDto.builder().vacancyId(1L).candidateId(1L).teamId(1L).createdBy(1L).build();

    @BeforeEach
    public void setup() {
        Mockito.when(userContext.getUserId()).thenReturn(1L);
    }

    @Test
    public void createOffer_Test() {
        service.createOffer(offerDto);

        Mockito.verify(offerRepository).save(offerMapper.toEntity(offerDto));
    }

    @Test
    public void deleteOffer_Test() {
        service.deleteOffer(offerDto);

        Mockito.verify(offerRepository).delete(offerMapper.toEntity(offerDto));
    }

    @Test
    public void updateOffer_Test() {
        service.updateOffer(offerDto);

        Mockito.verify(offerRepository).save(offerMapper.toEntity(offerDto));
    }
}