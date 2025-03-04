package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.events.FundRaisedEvent;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.PaymentFailedException;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.publishers.FundRaisedEventPublisher;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.validator.CampaignValidator;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DonationService {
    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final DonationMapper donationMapper;
    private final CampaignService campaignService;
    private final PaymentServiceClient paymentServiceClient;
    private final CampaignValidator campaignValidator;
    private final UserServiceClient userServiceClient;
    private final FundRaisedEventPublisher fundRaisedEventPublisher;

    @Transactional
    public DonationDto sendDonation(DonationCreateDto donationCreateDto, Long userId) {
        Donation donation = donationMapper.toEntity(donationCreateDto);
        Campaign campaign = campaignService.findCampaignById(donationCreateDto.getCampaignId());

        campaignValidator.validateCampaignStatus(campaign);

        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new DataValidationException("User with id " + userId + " not found");
        }

        long paymentNumber = System.currentTimeMillis() + userId;
        donation.setPaymentNumber(paymentNumber);

        try {
            if (!paymentServiceClient.sendPayment(new PaymentRequest(
                    paymentNumber,
                    donation.getAmount(),
                    donation.getCurrency()
            )).status().equals("SUCCESS")) {
                throw new PaymentFailedException("Payment with number " + paymentNumber + " failed");
            }
        } catch (FeignException e) {
            throw new PaymentFailedException("Payment with number " + paymentNumber + " failed", e);
        }

        donation.setCampaign(campaign);
        donation.setDonationTime(LocalDateTime.now());
        donation.setUserId(userId);

        campaign.setAmountRaised(campaign.getAmountRaised().add(donation.getAmount()));
        campaignRepository.save(campaign);

        Donation savedDonation = donationRepository.save(donation);
        fundRaisedEventPublisher.publish(new FundRaisedEvent(
                userId,
                campaign.getProject().getId(),
                donation.getAmount(),
                donation.getDonationTime()));

        return donationMapper.toDto(savedDonation);
    }

    @Transactional(readOnly = true)
    public DonationDto getDonationByIdAndUserId(Long donationId, Long userId) {
        Donation donation = donationRepository.findByIdAndUserId(donationId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Donation with id " + donationId
                        + " and user id " + userId + " not found"));
        return donationMapper.toDto(donation);
    }

    @Transactional(readOnly = true)
    public List<DonationDto> getAllDonationsByUser(Long userId, DonationFilterDto filters) {
        Stream<Donation> donations = donationRepository.findAllByUserIdFilteredAndThenSortedByDate(
                        userId,
                        filters.getStartDate(),
                        filters.getEndDate(),
                        filters.getCurrency(),
                        filters.getMaxAmount(),
                        filters.getMinAmount())
                .stream();

        return donations
                .map(donationMapper::toDto)
                .toList();
    }
}
