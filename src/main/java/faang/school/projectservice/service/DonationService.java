package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.DonationDto;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.filter.DonationFilterDto;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DonationService {
    private final CampaignService campaignService;
    private final PaymentServiceClient paymentServiceClient;
    private final DonationMapper donationMapper;
    private final DonationRepository donationRepository;

    @Transactional
    public DonationDto donateToCampaign(Long campaignId, PaymentRequest paymentRequest, long userId) {
        var campaign = campaignService.getById(campaignId);
        if (!campaign.getStatus().equals(CampaignStatus.ACTIVE)) {
            throw new RuntimeException("Campaign is not active");
        }

        var request = new PaymentRequest(new Random().nextLong(1000, 9000), paymentRequest.amount(),
                paymentRequest.actualCurrency(), campaign.getCurrency());
        var paymentResponse = paymentServiceClient.sendPayment(request);
        if (!paymentResponse.status().equals("SUCCESS")) {
            throw new RuntimeException("Payment failed");
        }

        campaignService.donateToCampaign(campaignId, paymentResponse.amount());

        var donation = Donation.builder()
                .amount(paymentResponse.amount())
                .donationTime(LocalDateTime.now())
                .paymentNumber(paymentResponse.paymentNumber())
                .userId(userId)
                .campaign(campaign)
                .build();

        donationRepository.save(donation);

        return donationMapper.toDto(donation);
    }

    public DonationDto getDonation(Long id, Long userId) {
        return donationMapper.toDto(getById(id, userId));
    }

    public List<DonationDto> getAllDonations(DonationFilterDto filterDto, Long userId) {
        var donations = donationRepository.findAllByUserId(userId);
        //TODO: add filter and page support
        //Pageable pageable = PageRequest.of(filterDto.getPage(), filterDto.getPageSize());
        //var statusName = isNull(filterDto.getStatus()) ? null : filterDto.getStatus().name();
        return donationMapper.toDto(donations);
    }

    private Donation getById(Long id, Long userId) {
        return donationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));
    }
}
