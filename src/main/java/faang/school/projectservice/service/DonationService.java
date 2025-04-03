package faang.school.projectservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final UserServiceClient userServiceClient;
    private final DonationMapper donationMapper;
    private final List<DonationFilter> donationFilters;
    private final UserContext userContext;

    public void createDonation(DonationDto donationDto) {
        validateDonation(donationDto);
        Long userId = userServiceClient.getUser(userContext.getUserId()).id();
        Donation donation = donationMapper.toEntity(donationDto);
        donation.setUserId(userId);
        Long paymentNumber = sendPayment(donation.getAmount(), donation.getCurrency());
        donation.setPaymentNumber(paymentNumber);
        donationRepository.save(
            donation
        );
    }

    public DonationDto getDonationByIdAndUserId(long id) {
        long userId = userContext.getUserId();
        userServiceClient.getUser(userId);
        return donationMapper.toDto(
                donationRepository.findByIdAndUserId(id, userId)
                        .orElseThrow(() -> {
                            log.error("Donation with id {} not found", id);
                            return new EntityNotFoundException(String.format("Donation with id %d not found", id));
                        })
        );
    }

    public List<DonationDto> getAllDonationsByUserId(DonationFilterDto donationFilterDto) {
        long userId = userContext.getUserId();
        userServiceClient.getUser(userId);
        Stream<Donation> donationStream = donationRepository.findAllByUserId(userId).stream();

        for (DonationFilter donationFilter : donationFilters) {
            if (donationFilter.isApplicable(donationFilterDto)) {
                donationStream = donationFilter.apply(donationStream, donationFilterDto);
            }
        }

        return donationMapper.toDto(donationStream.toList());
    }

    private void validateDonation(DonationDto donationDto) {
        Campaign campaign = campaignRepository.findById(donationDto.campaignId())
                .orElseThrow(() -> {
                    log.error("Campaign with id {} not found", donationDto.campaignId());
                    return new EntityNotFoundException(
                            String.format("Campaign with id %s not found", donationDto.campaignId())
                    );
                });
        if (!campaign.getStatus().equals(CampaignStatus.ACTIVE)) {
            throw new DataValidationException("Campaign status is not ACTIVE");
        }
    }

    private Long sendPayment(BigDecimal amount, Currency currency) {
        PaymentResponse paymentResponse = paymentServiceClient.sendPayment(
                new PaymentRequest(
                        UUID.randomUUID().getMostSignificantBits(),
                        amount,
                        currency
                )
        );

        if (!paymentResponse.status().equals("SUCCESS")) {
            throw new DataValidationException(
                    String.format("Payment failed: %s, %s", paymentResponse.status(), paymentResponse.message())
            );
        }

        return paymentResponse.paymentNumber();
    }

}
