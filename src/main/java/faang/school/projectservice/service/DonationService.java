package faang.school.projectservice.service;


import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donate.DonationCreateDto;
import faang.school.projectservice.dto.donate.DonationDto;
import faang.school.projectservice.dto.donate.DonationFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.PaymentFailedException;
import faang.school.projectservice.exception.PaymentServiceConnectException;
import faang.school.projectservice.exception.UserServiceConnectionException;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.filter.donation.DonationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationService {

    private static final long MIN_PAYMENT_NUMBER = 1000L;
    private static final long MAX_PAYMENT_NUMBER = 100000L;
    private final DonationRepository donationRepository;
    private final UserServiceClient userServiceClient;
    private final DonationMapper donationMapper;
    private final PaymentServiceClient paymentServiceClient;
    private final List<DonationFilter> donationFilters;
    private final CampaignService campaignService;
    private final Random random = new Random();


    public long getRandomNumber(long min, long max) {
        return random.nextLong(min, max);
    }

    @Transactional
    public DonationDto createDonation(DonationCreateDto donationDto) {

        UserDto user = getUserById(donationDto.userId());

        Campaign campaign = campaignService.getCampingById(donationDto.campaignId());

        Donation donation = donationMapper.toEntity(donationDto);
        donation.setDonationTime(LocalDateTime.now());
        donation.setUserId(user.id());
        donation.setCampaign(campaign);
        donation = donationRepository.save(donation);

        PaymentResponse paymentResponse = paymentToDonate(donationDto);

        if (!paymentResponse.status().equals("SUCCESS")) {
            log.error("Payment Failed ! {}, {}", donationDto, paymentResponse);
            donationRepository.delete(donation);
            throw new PaymentFailedException("Payment Failed !");
        }

        return donationMapper.toDto(donation);
    }

    protected PaymentResponse paymentToDonate(DonationCreateDto donationDto) {
        var paymentNumber = getRandomNumber(MIN_PAYMENT_NUMBER, MAX_PAYMENT_NUMBER);

        PaymentRequest paymentRequest = new PaymentRequest(
                paymentNumber,
                donationDto.amount(),
                donationDto.currency(),
                donationDto.currency()
        );

        try {
            return paymentServiceClient.sendPayment(paymentRequest);
        } catch (Exception e) {
            log.error("Payment service not working ! {}, {}", paymentRequest, e.getMessage());
            throw new PaymentServiceConnectException("Payment service not working !");
        }
    }

    public UserDto getUserById(Long userId) {
        try {
            var user = userServiceClient.getUser(userId);
            if (user == null) {
                log.error("User not found with id = {}", userId);
                throw new EntityNotFoundException("User not found with id = %s".formatted(userId));
            }
            return user;
        } catch (UserServiceConnectionException e) {
            log.error("User service not working !!!");
            throw new UserServiceConnectionException("User service not working !!!");
        }
    }

    public DonationDto getDonationById(Long id, Long userId) {
        return donationRepository
                .findByIdAndUserId(id, userId)
                .map(donationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));
    }

    public List<DonationDto> getUserDonations(Long userId, DonationFilterDto filterDto) {
        List<Donation> donations = donationRepository.findAllByUserId(userId);
        return donationFilters.stream()
                .filter(filter -> filter.isAcceptable(filterDto))
                .flatMap(filter -> filter.accept(donations.stream(), filterDto))
                .map(donationMapper::toDto)
                .toList();
    }

}