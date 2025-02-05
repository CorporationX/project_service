package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.validator.CampaignValidator;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DonationService {
    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final DonationMapper donationMapper;
    private final PaymentServiceClient paymentServiceClient;
    private final List<DonationFilter> donationFilters;
    private final CampaignValidator campaignValidator;
    private final UserServiceClient userServiceClient;

    public DonationDto sendDonation(Long userId, DonationCreateDto donationCreateDto) {
        Donation donation = donationMapper.toEntity(donationCreateDto);
        Campaign campaign = campaignRepository.findById(donationCreateDto.getCampaignId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Campaign not found with id: " + donationCreateDto.getCampaignId()));

        campaignValidator.validateCampaignStatus(campaign);

        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new DataValidationException("User with id " + userId + " not found");
        }

        paymentServiceClient.sendPayment(new PaymentRequest(
                donation.getPaymentNumber(),
                donation.getAmount(),
                donation.getCurrency()
        ));

        donation.setCampaign(campaign);
        donation.setPaymentNumber(System.currentTimeMillis());
        donation.setDonationTime(LocalDateTime.now());
        donation.setUserId(userId);

        campaign.setAmountRaised(campaign.getAmountRaised().add(donation.getAmount()));
        campaignRepository.save(campaign);

        return donationMapper.toDto(donationRepository.save(donation));
    }

    public DonationDto getDonationByIdAndUserId(Long donationId, Long userId) {
        Donation donation = donationRepository.findByIdAndUserId(donationId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));
        return donationMapper.toDto(donation);
    }

    public List<DonationDto> getAllDonationsByUser(Long userId, DonationFilterDto filters) {
        Stream<Donation> donations = donationRepository.findAllByUserIdAndSortedByDate(userId).stream();
        if (filters != null) {
            for (DonationFilter filter : donationFilters) {
                if (filter.isApplicable(filters)) {
                    donations = filter.apply(donations, filters);
                }
            }
        }
        return donations
                .map(donationMapper::toDto)
                .toList();
    }
}
