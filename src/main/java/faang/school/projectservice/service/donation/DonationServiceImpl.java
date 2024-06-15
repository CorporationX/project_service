package faang.school.projectservice.service.donation;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.filter.DonationFilterDto;
import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.donation.filter.DonationFilterService;
import faang.school.projectservice.validation.donation.DonationValidator;
import faang.school.projectservice.validation.user.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationMapper donationMapper;
    private final DonationValidator donationValidator;
    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final UserValidator userValidator;
    private final DonationFilterService donationFilterService;
    private final PaymentServiceClient paymentServiceClient;

    @Override
    @Transactional
    public DonationDto sendDonation(long userId, DonationCreateDto donationDto) {

        Campaign campaign = campaignRepository.findById(donationDto.getCampaignId())
                .orElseThrow(() -> new NotFoundException(String.format("Campaign with id %d not found",
                        donationDto.getCampaignId())));

        Donation donation = donationMapper.toEntity(donationDto);
        donation.setCampaign(campaign);
        donation.setUserId(userId);

        userValidator.validateUserExistence(userId);
        donationValidator.validateSendDonation(donationDto);

        sendPaymentRequest(donation);

        donationRepository.save(donation);
        log.info("Saved donation {} to user {}", donation.getId(), userId);

        return donationMapper.toDto(donation);
    }

    @Override
    @Transactional(readOnly = true)
    public DonationDto getDonationById(long donationId) {

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new NotFoundException(String.format("Donation with id %d not found", donationId)));

        return donationMapper.toDto(donation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonationDto> getAllDonationsByUserId(long userId, DonationFilterDto filter) {

        userValidator.validateUserExistence(userId);
        return donationFilterService.applyFilters(donationRepository.findAllByUserId(userId).stream(), filter)
                .sorted(Comparator.comparing(Donation::getDonationTime).reversed())
                .map(donationMapper::toDto)
                .toList();
    }

    private void sendPaymentRequest(Donation donation) {
        PaymentRequest paymentRequest = donationMapper.toPaymentRequest(donation);
        paymentServiceClient.sendPayment(paymentRequest);
        log.info("Sent PaymentRequest: {}", paymentRequest);
    }
}
