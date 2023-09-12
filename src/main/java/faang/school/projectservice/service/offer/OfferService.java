package faang.school.projectservice.service.offer;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.offer.OfferDto;
import faang.school.projectservice.mapper.offer.OfferMapper;
import faang.school.projectservice.model.Offer;
import faang.school.projectservice.repository.OfferRepository;
import faang.school.projectservice.validator.offer.OfferValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final UserContext userContext;
    private final OfferValidator offerValidator;
    private final OfferMapper offerMapper;
    private final OfferRepository offerRepository;

    @Transactional
    public OfferDto createOffer(OfferDto offerDto) {
        Long userId = userContext.getUserId();
        Offer offer = offerMapper.toEntity(offerDto);
        offerValidator.createOfferServiceValidation(offer, userId);
        return offerMapper.toDto(offerRepository.save(offer));
    }

    @Transactional
    public void deleteOffer(OfferDto offerDto) {
        Long userId = userContext.getUserId();
        offerValidator.defaultOfferServiceValidation(offerDto, userId);
        offerRepository.delete(offerMapper.toEntity(offerDto));
    }

    @Transactional
    public OfferDto updateOffer(OfferDto offerDto) {
        Long userId = userContext.getUserId();
        offerValidator.defaultOfferServiceValidation(offerDto, userId);
        return offerMapper.toDto(offerRepository.save(offerMapper.toEntity(offerDto)));
    }
}
