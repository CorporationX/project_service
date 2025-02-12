package faang.school.projectservice.service.donation;

import faang.school.projectservice.config.context.user.UserContext;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.payment.CampaignNotActiveException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.payment.PaymentService;
import faang.school.projectservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class DonationService {
    private final PaymentService paymentService;
    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final UserService userService;
    private final UserContext userContext;
    private final List<DonationFilter> donationFilters;

    @Transactional
    public Donation createDonation(Donation donation) {
        long userId = getUserId();
        long campaignId = donation.getCampaign().getId();

        log.info("Creating donation for campaign ID {} by user ID {}", campaignId, userId);

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> {
                    log.error("Campaign not found by ID: {}", campaignId);
                    return new NoSuchElementException("Campaign not found");
                });

        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            log.warn("Attempt to donate to inactive campaign ID {}", campaignId);
            throw new CampaignNotActiveException("Campaign is not active");
        }

        log.debug("Processing payment for amount {} {}", donation.getAmount(), donation.getCurrency());
        PaymentResponse paymentResponse = paymentService.makePayment(
                donation.getAmount(), donation.getCurrency());

        donation.setPaymentNumber(paymentResponse.paymentNumber());
        donation.setCampaign(campaign);
        donation.setUserId(userId);

        Donation savedDonation = donationRepository.save(donation);
        log.info("Donation created successfully {}", savedDonation);

        return savedDonation;
    }

    @Transactional(readOnly = true)
    public Donation getDonationById(long donationId) {
        long userId = getUserId();
        log.debug("Fetching donation ID {} for user ID {}", donationId, userId);

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> {
                    log.warn("Donation not found by ID: {}", donationId);
                    return new NoSuchElementException("Donation not found");
                });

        if (donation.getUserId() != userId) {
            log.warn("Access denied: User ID {} tried to access donation ID {}", userId, donationId);
            throw new IllegalStateException("Access to donation denied");
        }

        return donation;
    }

    @Transactional(readOnly = true)
    public List<Donation> getAllUserDonations(DonationFilterDto dtoFilters) {
        long userId = getUserId();
        log.info("Fetching all donations for user ID {} with filters: {}", userId, dtoFilters);

        Stream<Donation> donations = donationRepository.findAllByUserId(userId).stream();
        List<Donation> filteredDonations = filterDonations(donations, dtoFilters).toList();

        log.debug("Found {} donations for user ID {} after filtering", filteredDonations.size(), userId);
        return filteredDonations;
    }

    private Stream<Donation> filterDonations(Stream<Donation> donations, DonationFilterDto dtoFilters) {
        List<DonationFilter> applicableFilters = donationFilters.stream()
                .filter(donationFilter -> donationFilter.isApplicable(dtoFilters))
                .toList();

        log.debug("Applying {} filters to donations", applicableFilters.size());
        return donations.filter(donation ->
                        applicableFilters.stream()
                                .allMatch(donationFilter ->
                                        donationFilter.apply(donation, dtoFilters)))
                .sorted(Comparator.comparing(Donation::getDonationTime).reversed());
    }

    private long getUserId() {
        long userId = userContext.getUserId();
        log.debug("Retrieving user ID from context: {}", userId);
        userService.getUser(userId);
        return userId;
    }
}
