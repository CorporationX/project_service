package faang.school.projectservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.DonationFilter;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.model.Donation;
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
    private final CampaignService campaignService;
    private final PaymentServiceClient paymentServiceClient;
    private final UserServiceClient userServiceClient;
    private final DonationMapper donationMapper;
    private final List<DonationFilter> donationFilters;

    public void createDonation(DonationDto donationDto) {
        validateDonation(donationDto);
        userServiceClient.getUser(donationDto.userId());
        Donation donation = donationMapper.toEntity(donationDto);
        Long paymentNumber = sendPayment(donation.getAmount(), donation.getCurrency());
        donation.setPaymentNumber(paymentNumber);
        donationRepository.save(
            donation
        );
    }

    public DonationDto getDonationByIdAndUserId(long id, long userId) {
        userServiceClient.getUser(userId);
        return donationMapper.toDto(
                donationRepository.findByIdAndUserId(id, userId)
                        .orElseThrow(EntityNotFoundException::new)
        );
    }

    public List<DonationDto> getAllDonationsByUserId(Long userId, DonationFilterDto donationFilterDto) {
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
        CampaignDto campaignDto = campaignService.getCampaignById(donationDto.campaignId());
        if (!campaignDto.status().equals("ACTIVE")) {
            throw new DataValidationException("Campaign status is not ACTIVE");
        }
    }

    private Long sendPayment(BigDecimal amount, Currency currency) {
        PaymentResponse paymentResponse = paymentServiceClient.sendPayment(
                new PaymentRequest(
                        1L,
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
