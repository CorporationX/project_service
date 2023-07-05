package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.DonationDto;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.filter.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/donate")
@RequiredArgsConstructor
public class DonationController {
    private final UserContext userContext;
    private final DonationService donationService;

    @GetMapping("/{id}")
    public DonationDto get(@PathVariable Long id) {
        return donationService.getDonation(id, userContext.getUserId());
    }

    @GetMapping
    public List<DonationDto> getAll(DonationFilterDto filter) {
        return donationService.getAllDonations(filter, userContext.getUserId());
    }

    @PostMapping("/{campaignId}/donate")
    public DonationDto donateToCampaign(@PathVariable("campaignId") Long campaignId, @RequestBody PaymentRequest request) {
        return donationService.donateToCampaign(campaignId, request, userContext.getUserId());
    }
}
