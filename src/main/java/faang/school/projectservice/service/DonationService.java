package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DonationService {
    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final UserServiceClient userServiceClient;
    private final DonationMapper donationMapper;

    public DonationDto sendDonation(UserDto userDto, CampaignDto campaignDto, DonationDto donationDto) {
        donationDto.setUserId(validUser(userDto.getId()).getId());
        donationDto.setCampaignDto(campaignDto);
        PaymentRequest paymentRequest = new PaymentRequest(donationDto.getPaymentNumber(), donationDto.getAmount(), donationDto.getCurrency(), donationDto.getCurrency());
        paymentServiceClient.sendPayment(paymentRequest);
        Donation donation = donationRepository.save(donationMapper.toDonation(donationDto));
        return donationMapper.toDonationDto(donation);
    }

    public UserDto validUser(long id) {
        return userServiceClient.getUser(id);
    }
}
