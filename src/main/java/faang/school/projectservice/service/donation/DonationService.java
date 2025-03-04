package faang.school.projectservice.service.donation;

import faang.school.projectservice.client.CurrencyConverter;
import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.DonationNotFoundException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final DonationMapper donationMapper;
    private final PaymentServiceClient paymentServiceClient;
    private final UserServiceClient userServiceClient;
    private final List<DonationFilter> donationFilters;
    private final CurrencyConverter currencyConverter;
    private final CampaignRepository campaignRepository;

    public DonationDto sendDonation(DonationDto donation) {
        userServiceClient.getUser(donation.getUserId());
        //Перевод валюты из donation в campaign
        try {
            campaignRepository.getById(donation.getCampaignId());
        } catch (NullPointerException e) {
            log.error("Campaign does not exist : {e}", e);
        }
        currencyConverter.converter(donation);

        PaymentRequest paymentRequest = new PaymentRequest(
                donation.getPaymentNumber(),
                donation.getAmount(),
                donation.getCurrency()
        );

        try {
            paymentServiceClient.sendPayment(paymentRequest);
        } catch (Exception e) {
            log.error("Donation donation did no send {}", e);
        }
        log.info("Payment number ID: {} sent", donation.getPaymentNumber());
        Donation donationEntity = donationMapper.toEntity(donation);
        return donationMapper.toDto(donationRepository.save(donationEntity));

    }

    public DonationDto getDonation(Long userId, Long donationId) {
        return donationRepository.findByIdAndUserId(donationId, userId)
                .map(donationMapper::toDto)
                .orElseThrow(() -> new DonationNotFoundException("Donation not found for userId: "
                        + userId
                        + " and donationId: "
                        + donationId));
    }

    public List<DonationDto> getAllDonationsUser(Long userId, DonationFilterDto filterDto) {
        userServiceClient.getUser(userId);

        List<Donation> donations = donationRepository.findAllByUserId(userId);

        if (donations.isEmpty()) {
            return Collections.emptyList();
        }

        if (filterDto != null) {
            for (DonationFilter filter : donationFilters) {
                if (filter.isApplicable(filterDto)) {
                    donations = filter.apply(donations, filterDto);
                }
            }
        }

        return donations.stream()
                .map(donationMapper::toDto)
                .sorted(Comparator.comparing(DonationDto::getDonationDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }
}