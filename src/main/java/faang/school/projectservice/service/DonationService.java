package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityStatusException;
import faang.school.projectservice.exception.UserNotFoundException;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final DonationMapper donationMapper;
    private final PaymentServiceClient paymentServiceClient;
    private final UserServiceClient userServiceClient;

    @Transactional
    public DonationDto send(DonationDto donationDto) {
        isUserExist(donationDto.getUserId());

        Optional<Campaign> campaignById = campaignRepository.findById(donationDto.getCampaignId());
        campaignById.orElseThrow(() -> new DataValidationException("No such campaign found."));
        Campaign campaign = campaignById.get();
        validateStatus(campaign);

        paymentServiceClient.sendPayment(
                new PaymentRequest(donationDto.getPaymentNumber(), donationDto.getAmount(), donationDto.getCurrency()));

        Donation donation = donationMapper.toEntity(donationDto);
        donationRepository.save(donation);
        return donationMapper.toDto(donation);
    }

    public DonationDto getDonation(long donationId) {
        Optional<Donation> donationById = donationRepository.findById(donationId);
        donationById.orElseThrow(() -> new DataValidationException("Donation does not exist"));
        return donationMapper.toDto(donationById.get());
    }

    public List<DonationDto> getDonationsByUserId(long userId) {
        isUserExist(userId);
        List<Donation> donations = donationRepository.findAll();
        List<Donation> donationsByUserId = new ArrayList<>();
        for (Donation donation : donations) {
            if (donation.getUserId() == userId) {
                donationsByUserId.add(donation);
            }
        }
        return donationsByUserId
                .stream()
                .map(donation -> donationMapper.toDto(donation))
                .toList();
    }

    private void validateStatus(Campaign campaign) {
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new EntityStatusException("Campaign is not active");
        }
    }

    private void isUserExist(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.FeignClientException exception) {
            throw new UserNotFoundException("This user doesn't exist");
        }
    }
}
