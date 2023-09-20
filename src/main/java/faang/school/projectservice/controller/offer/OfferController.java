package faang.school.projectservice.controller.offer;

import faang.school.projectservice.dto.offer.OfferDto;
import faang.school.projectservice.service.offer.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/offer")
@RequiredArgsConstructor
public class OfferController {
    private final OfferService service;

    @PostMapping("/create")
    public OfferDto createOffer(@RequestBody OfferDto offerDto) {
        return service.createOffer(offerDto);
    }

    @PostMapping("/delete")
    public void deleteOffer(@RequestBody OfferDto offerDto) {
        service.deleteOffer(offerDto);
    }

    @PostMapping("/update")
    public OfferDto updateOffer(@RequestBody OfferDto offerDto) {
        return service.updateOffer(offerDto);
    }
}
