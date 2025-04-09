package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.CampaignNotActiveException;
import faang.school.projectservice.exception.DifferentCurrencyException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.mapper.donation.PaymentMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationService {

    private static final String CAMPAIGN_ENTITY_NAME = "campaign";
    private static final String DONATION_ENTITY_NAME = "donation";

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final DonationMapper donationMapper;
    private final PaymentMapper paymentMapper;
    private final PaymentServiceClient paymentClient;
    private final List<DonationFilter> filters;
    private final UserContext userContext;
    private final UserServiceClient userClient;

    @Transactional
    public PaymentResponse sendDonation(DonationDto donationDto) {
        Long userId = userClient.getUser(userContext.getUserId()).id();
        Donation donation = donationMapper.dtoToEntity(donationDto);
        Long campaignId = donationDto.campaignId();
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException(CAMPAIGN_ENTITY_NAME, campaignId));
        Long paymentNumber = createRandomPaymentNumber();

        Objects.requireNonNull(campaign.getStatus(), "Campaign hasn't status");
        if (!campaign.getStatus().equals(CampaignStatus.ACTIVE)) {
            throw new CampaignNotActiveException("Campaign with id %d not active", campaignId);
        }
        donation.setCampaign(campaign);
        donation.setUserId(userId);
        donation.setPaymentNumber(paymentNumber);

        donationRepository.save(donation);
        log.debug("New donation (id: {}) save on DB", donation.getId());

        PaymentRequest request = paymentMapper.donationToPaymentRequest(donation);

        Objects.requireNonNull(campaign.getCurrency(), "Campaign hasn't currency");

        if (!campaign.getCurrency().equals(request.currency())) {
            throw new DifferentCurrencyException("Request currency (%s) does not match campaign currency (%s)",
                    request.currency(), campaign.getCurrency());
        }
        PaymentResponse response = paymentClient.sendPayment(request);
        log.info("\n{}\nStatus: {}\nYour verification code: {}",
                response.message(), response.status(), response.verificationCode());
        return response;
    }

    public DonationDto findDonationById(Long donationId) {
        Donation donation = donationRepository.findByIdAndUserId(donationId, userContext.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(DONATION_ENTITY_NAME, donationId));

        return donationMapper.entityToDto(donation);
    }

    public List<DonationDto> findDonationsByFilters(DonationFilterDto filter) {
        Specification<Donation> specifications = filters.stream()
                .filter(donationFilter -> donationFilter.isApplicable(filter))
                .map(donationFilter -> donationFilter.apply(filter))
                .reduce(Specification::and)
                .orElse(null);

        List<Donation> donations = specifications != null ? donationRepository.findAll(specifications)
                : donationRepository.findAll();

        return donationMapper.entityListToDtoList(donations.stream()
                .filter(donation -> donation.getUserId().equals(userContext.getUserId()))
                .sorted(Comparator.comparing(Donation::getDonationTime).reversed())
                .toList());
    }

    private Long createRandomPaymentNumber() {
        long paymentNumber = UUID.randomUUID().getMostSignificantBits();
        if (paymentNumber == Long.MIN_VALUE) {
            return Math.abs(paymentNumber + 1);
        }
        return Math.abs(paymentNumber);
    }
}
