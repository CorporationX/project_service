package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.validator.DonationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationService {
    private final CampaignRepository campaignRepository;
    private final UserContext userContext;
    private final DonationRepository donationRepository;
    private final List<DonationFilter> donationFilters;
    private final DonationMapper donationMapper;
    private final PaymentServiceClient paymentServiceClient;
    private final DonationValidator donationValidator;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentResponse createDonation(DonationDto donationDto) {
        Campaign campaign = getCampaign(donationDto);

        donationValidator.validateCampaign(campaign);

        long paymentNumber = System.currentTimeMillis();

        PaymentRequest paymentRequest = createPaymentRequest(donationDto, paymentNumber);

        PaymentResponse paymentResponse = paymentServiceClient.sendPayment(paymentRequest);

        donationValidator.validatePaymentResponse(paymentResponse, paymentNumber);

        Long userId = userContext.getUserId();

        Donation donation = donationMapper.toEntity(donationDto);
        donation.setUserId(userId);
        donation.setPaymentNumber(paymentNumber);

        donationRepository.save(donation);

        log.info("Donation (id = {}) successfully created and saved to database", donation.getId());

        return paymentResponse;
    }

    @Transactional(readOnly = true)
    public DonationDto getDonationById(Long donationId) {
        Long userId = userContext.getUserId();

        Donation donation = donationRepository.findByIdAndUserId(donationId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));

        return donationMapper.toDto(donation);
    }

    @Transactional(readOnly = true)
    public List<DonationDto> getAllDonations() {
        Long userId = userContext.getUserId();

        List<Donation> donations = donationRepository.findAllByUserId(userId);

        return donationMapper.toDtoList(donations);
    }

    @Transactional(readOnly = true)
    public List<DonationDto> getAllDonationsByFilters(DonationFilterDto filters) {
        Long userId = userContext.getUserId();
        List<Donation> donations = donationRepository.findAllByUserId(userId);

        if (donations.isEmpty()) {
            return List.of();
        }

        donationFilters.stream()
                .filter(donationFilter -> donationFilter.isApplicable(filters))
                .forEach(donationFilter -> donationFilter.apply(donations, filters));

        return donationMapper.toDtoList(donations);
    }

    private PaymentRequest createPaymentRequest(DonationDto donationDto, long paymentNumber) {
        return PaymentRequest.builder()
                .paymentNumber(paymentNumber)
                .amount(donationDto.getAmount())
                .paymentCurrency(Currency.valueOf(donationDto.getCurrency()))
                .targetCurrency(Currency.valueOf(donationDto.getCurrency()))
                .build();
    }

    private Campaign getCampaign(DonationDto donationDto) {
        return campaignRepository.findById(donationDto.getCampaignId())
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found"));
    }
}
