package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.filter.donation.DonationFilters;
import faang.school.projectservice.filter.donation.FilterDonationDto;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.model.campaign.Campaign;
import faang.school.projectservice.model.campaign.CampaignStatus;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import faang.school.projectservice.exception.DonationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class DonationService {
    private final DonationRepository donationRepository;
    private final DonationMapper donationMapper;
    private final PaymentServiceClient paymentServiceClient;
    private final UserServiceClient userServiceClient;
    private final List<DonationFilters> donationFilters;
    private final CampaignRepository campaignRepository;

    @Transactional
    public DonationDto sendDonation(DonationDto donationDto) {
        if (isEmpty(donationDto.getUserId(), donationDto.getCampaign())) {
            throw new DonationException("User or project not found");
        }

        if (!isActiveCampaign(donationDto.getCampaign())) {
            throw new DonationException("Campaign is not active");
        }

        isUserExists(donationDto.getUserId());

        paymentServiceClient.sendPayment(
                new PaymentRequest(donationDto.getPaymentNumber(), donationDto.getAmount(), donationDto.getCurrency())
        );

        Donation donation = donationMapper.toEntity(donationDto);

        return donationMapper.toDto(donationRepository.save(donation));
    }

    public DonationDto getDonation(Long id) {
        return donationMapper.toDto(donationRepository.findById(id).orElseThrow(() -> new DonationException("Donation not found")));
    }

    public List<DonationDto> getDonations(Long userId) {
        isUserExists(userId);
        return donationRepository.findAllByUserId(userId).stream().map(donationMapper::toDto).toList();
    }

    public List<Donation> getDonationsFilter(FilterDonationDto filterDonationDto, Long userId) {

        Stream<Donation> donation = donationRepository.findAllByUserId(userId).stream().sorted(Comparator.comparing(Donation::getDonationTime));
        List<DonationFilters> usedFilters = donationFilters.stream()
                .filter(filter -> filter.isApplicable(filterDonationDto))
                .toList();
        for (DonationFilters filter : usedFilters) {
            donation = filter.apply(donation, filterDonationDto);
        }

        return donation.toList();
    }

    private boolean isEmpty(Long userId, Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new DonationException("Campaign not found"));
        return userId == null && campaign == null;
    }

    private boolean isActiveCampaign(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new DonationException("Campaign not found"));
        return campaign.getStatus().equals(CampaignStatus.ACTIVE);
    }

    private void isUserExists(Long userId) {
        try {
            userServiceClient.getUser(userId);
        }catch (Exception e) {
            throw new DonationException("User not found");
        }
    }

}
