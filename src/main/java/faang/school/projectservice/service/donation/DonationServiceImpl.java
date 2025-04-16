package faang.school.projectservice.service.donation;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationCreateRequest;
import faang.school.projectservice.dto.donation.DonationResponse;
import faang.school.projectservice.dto.donation.SearchDonationDto;
import faang.school.projectservice.exception.campaign.CampaignCanceledException;
import faang.school.projectservice.exception.campaign.CampaignCompletedException;
import faang.school.projectservice.exception.campaign.CampaignExceptionMessage;
import faang.school.projectservice.exception.campaign.UnknownCampaignStatusException;
import faang.school.projectservice.exception.donation.DonationExceptionMessage;
import faang.school.projectservice.exception.donation.DonationNotFoundException;
import faang.school.projectservice.exception.donation.ExceedDonationAmountException;
import faang.school.projectservice.exception.payment.PaymentExceptionMessage;
import faang.school.projectservice.exception.payment.PaymentFailedException;
import faang.school.projectservice.exception.user.UserExceptionMessage;
import faang.school.projectservice.exception.user.UserNotFoundException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.campaign.CampaignService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private static final long MIN_PAYMENT_NUMBER = 1000_0000_0000_0000L;
    private static final long MAX_PAYMENT_NUMBER = 9999_9999_9999_9999L;

    private final DonationRepository donationRepository;
    private final DonationMapper donationMapper;
    private final CampaignService campaignService;
    private final PaymentServiceClient paymentServiceClient;
    private final UserServiceClient userServiceClient;
    private final List<DonationFilter> donationFilters;

    @Override
    @Transactional
    public DonationResponse createDonation(long userId, DonationCreateRequest donationCreateRequest, long campaignId) {
        //todo: метод валидации будет работать только после появления эндпоинта в UserService
        validationUserId(userId);

        Campaign campaign = campaignService.findById(campaignId);
        validationCampaignStatus(campaign);

        validationDonationAmount(donationCreateRequest, campaign);

        log.info("Starting payment processing...");
        PaymentResponse paymentResponse = sendPayment(donationCreateRequest, campaign);
        log.info("""
                Payment Response:
                - Payment Status: {}
                - Payment Number: {}
                - Message: {}""",
                paymentResponse.status(),
                paymentResponse.paymentNumber(),
                paymentResponse.message());
        validationPaymentStatus(paymentResponse);

        log.info("Starting donation persistence...");
        campaign.setAmountRaised(campaign.getAmountRaised().add(paymentResponse.amount()));

        Donation donation = donationMapper.toEntity(donationCreateRequest);
        donation.setPaymentNumber(paymentResponse.paymentNumber());
        donation.setDonationTime(LocalDateTime.now());
        donation.setCampaign(campaign);
        donation.setUserId(userId);

        donationRepository.save(donation);
        log.info("Donation successfully persisted. Donation ID: {}", donation.getId());

        return donationMapper.toResponse(donation);
    }

    @Override
    public DonationResponse getDonation(long donationId, long userId) {
        //todo: метод валидации будет работать только после появления эндпоинта в UserService
        validationUserId(userId);

        log.info("Starting donation searching for user '{}'...", userId);
        Donation donation = donationRepository.findByIdAndUserId(donationId, userId)
                .orElseThrow(() -> new DonationNotFoundException(DonationExceptionMessage.NOT_FOUND));
        log.info("Donation successfully searched. Donation ID: {}", donationId);

        return donationMapper.toResponse(donation);
    }

    @Override
    public List<DonationResponse> getDonations(long userId, SearchDonationDto searchDonationDto) {
        //todo: метод валидации будет работать только после появления эндпоинта в UserService
        validationUserId(userId);

        Stream<Donation> filteredDonations = donationRepository.findAllByUserId(userId).stream();

        log.info("Starting donation filtering from user '{}'...", userId);
        for (DonationFilter donationFilter : donationFilters) {
            if (donationFilter.isApplicable(searchDonationDto)) {
                filteredDonations = donationFilter.apply(filteredDonations, searchDonationDto);
            }
        }
        log.info("Donations successfully filtered");

        return filteredDonations
                .map(donationMapper::toResponse)
                .sorted(Comparator.comparing(DonationResponse::getDonationTime).reversed())
                .toList();
    }

    private void validationUserId(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException exception) {
            log.error(exception.getMessage());
            throw new UserNotFoundException(UserExceptionMessage.getNotFound(userId));
        }
    }

    private void validationCampaignStatus(Campaign campaign) {
        long id = campaign.getId();
        CampaignStatus status = campaign.getStatus();

        switch (status) {
            case ACTIVE:
                break;
            case COMPLETED:
                throw new CampaignCompletedException(CampaignExceptionMessage.getCompleted(id));
            case CANCELED:
                throw new CampaignCanceledException(CampaignExceptionMessage.getCanceled(id));
            default:
                throw new UnknownCampaignStatusException(CampaignExceptionMessage.getUnknownStatus(status));
        }
    }

    private static void validationDonationAmount(DonationCreateRequest donationCreateRequest, Campaign campaign) {
        if (donationCreateRequest.getAmount().compareTo(campaign.getGoal().subtract(campaign.getAmountRaised())) > 0) {
            throw new ExceedDonationAmountException(DonationExceptionMessage.EXCEED_AMOUNT);
        }
    }

    private void validationPaymentStatus(PaymentResponse paymentResponse) {
        if (!paymentResponse.status().equals("SUCCESS")) {
            throw new PaymentFailedException(PaymentExceptionMessage.getFailed(paymentResponse.message()));
        }
    }

    private PaymentResponse sendPayment(DonationCreateRequest donation, Campaign campaign) {
        PaymentRequest paymentRequest = new PaymentRequest(
                new Random().nextLong(MIN_PAYMENT_NUMBER, MAX_PAYMENT_NUMBER),
                donation.getAmount(),
                donation.getCurrency(),
                campaign.getCurrency()
        );

        return paymentServiceClient.sendPayment(paymentRequest);
    }
}
