package faang.school.projectservice.service.donation;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.CreateDonationDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final UserContext userContext;
    private final DonationMapper donationMapper;
    private final CampaignRepository campaignRepository;
    private final UserServiceClient userServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final List<DonationFilter> donationFilters;


    @Override
    @Retryable(retryFor = {FeignException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public DonationDto sendDonation(CreateDonationDto createDonationDto) {
        if (createDonationDto.targetCurrency() == null) {
            createDonationDto = createDonationDto.withTargetCurrency(createDonationDto.paymentCurrency());
        }

        Campaign campaign = campaignRepository.getByIdOrThrow(createDonationDto.campaignId());
        log.debug("Got campaign {} from base", campaign.getId());

        if (!campaign.getStatus().equals(CampaignStatus.ACTIVE)) {
            String errorMessage = "Campaign %d is not active. Cant send donation for none active campaignDto"
                    .formatted(campaign.getId());
            log.error(errorMessage);
            throw new ForbiddenException(errorMessage);
        }

        PaymentRequest paymentRequest = new PaymentRequest(generatePaymentNumber(), createDonationDto.amount(),
                createDonationDto.paymentCurrency(), createDonationDto.targetCurrency());
        PaymentResponse paymentResponse = paymentServiceClient.sendPayment(paymentRequest);

        Donation donation = donationMapper.toDonation(createDonationDto);
        donation.setCampaign(campaign);
        donation.setDonationTime(LocalDateTime.now());
        donation.setUserId(userContext.getUserId());
        donation.setPaymentNumber(paymentResponse.paymentNumber());

        log.info("Sent payment. Payment number {}, amount {}, paymentCurrency currency {}, target currency {}",
                paymentRequest.paymentNumber(), paymentRequest.amount(), paymentRequest.paymentCurrency(),
                paymentRequest.targetCurrency());

        Donation savedDonation = donationRepository.save(donation);
        log.info("Donation {} has been sent", savedDonation.getId());
        return donationMapper.toDonationDto(savedDonation);
    }

    @Override
    public List<DonationDto> getDonationsByUserId(long userId, DonationFilterDto donationFilterDto) {
        validateUser(userId);
        Stream<Donation> donationStream = donationRepository.findAllByUserId(userId).stream();
        log.debug("Got all donations for user {}", userId);

        for (DonationFilter donationFilter : donationFilters) {
            if (donationFilter.isApplicable(donationFilterDto)) {
                donationStream = donationFilter.apply(donationStream, donationFilterDto);
            }
        }

        return donationStream.map(donationMapper::toDonationDto).toList();
    }

    @Override
    public DonationDto getDonationByIdAndUserId(long donationId, long userId) {
        validateUser(userId);
        Donation donation = donationRepository.findByIdAndUserId(donationId, userId).orElseThrow(
                () -> {
                    String errorMessage = "Donation %d for user %d not found".formatted(donationId, userId);
                    log.error(errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });
        log.debug("Donation %d for user %d has been found".formatted(donationId, userId));
        return donationMapper.toDonationDto(donation);
    }

    private long generatePaymentNumber() {
        long millis = System.currentTimeMillis();
        int randomInt = ThreadLocalRandom.current().nextInt(1000, 9999);
        return millis + randomInt;
    }

    @Retryable(retryFor = {FeignException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    private void validateUser(long userId) {
        UserDto user = userServiceClient.getUser(userId);
        log.debug("User {} found", userId);
        if (user == null || user.id() == null || !user.id().equals(userId)) {
            String errorMessage = "User %d not found".formatted(userId);
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
    }
}