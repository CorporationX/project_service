package faang.school.projectservice.service.impl;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.DonationDto;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.filter.DonationFilterDto;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {
    private final DonationRepository donationRepository;
    private final UserServiceClient userServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final DonationMapper donationMapper;
    private final UserContext userContext;
    private final CampaignRepository campaignRepository;


    @Override
    public DonationDto sendDonation(DonationDto donationDto) {
        Long userId = userContext.getUserId();
        if (userId == null) {
            throw new IllegalStateException("User ID is missing in the request headers");
        }

        Campaign campaign = campaignRepository.findById(donationDto.campaignId())
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        PaymentRequest paymentRequest = new PaymentRequest(
                userId,
                donationDto.amount(),
                donationDto.currency(),
                donationDto.currency());

        paymentServiceClient.sendPayment(paymentRequest);

        Donation donation = donationMapper.toEntity(donationDto);
        donation.setUserId(userId);

        return donationMapper.toDto(donationRepository.save(donation));
    }

    @Override
    public DonationDto getDonation(Long donationId, Long userId) {
        return donationMapper.toDto(donationRepository.findByIdAndUserId(donationId, userId)
                .orElseThrow(() -> new RuntimeException("Donation not found")));
    }

    @Override
    public List<DonationDto> getDonations(Long userId, DonationFilterDto donationFilterDto) {
        return donationRepository.findAllByUserId(userId).stream()
                .filter(donation -> donationFilterDto.startDate() == null || donation.getDonationTime()
                        .isAfter(donationFilterDto.startDate()))
                .filter(donation -> donationFilterDto.endDate() == null || donation.getDonationTime()
                        .isBefore(donationFilterDto.endDate()))
                .filter(donation -> donationFilterDto.currency() == null || donation.getCurrency()
                        .equals(donationFilterDto.currency()))
                .filter(donation -> donationFilterDto.minAmount() == null || donation.getAmount()
                        .compareTo(donationFilterDto.minAmount()) >= 0)
                .filter(donation -> donationFilterDto.maxAmount() == null || donation.getAmount()
                        .compareTo(donationFilterDto.maxAmount()) <= 0)
                .sorted((d1, d2) -> d2.getDonationTime().compareTo(d1.getDonationTime()))
                .map(donationMapper::toDto)
                .collect(Collectors.toList());
    }
}
