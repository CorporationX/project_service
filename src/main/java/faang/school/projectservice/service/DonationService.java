package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.util.validator.DonationServiceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DonationService {

    private final DonationServiceValidator donationServiceValidator;

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;

    private final DonationMapper donationMapper;

    private final PaymentServiceClient paymentServiceClient;
    private final UserServiceClient userServiceClient;

    public DonationDto send(DonationDto donationDto) {
        donationServiceValidator.isUserExist(userServiceClient, donationDto); /* по какой-то причине user не находится и кидается ошибка
        "This user doesn't exist" в валидаторе */

        Optional<Campaign> campaignById = campaignRepository.findById(donationDto.getCampaignId());
        campaignById.orElseThrow(() -> new DataValidationException("No such campaign found."));
        Campaign campaign = campaignById.get();
        donationServiceValidator.validateStatus(campaign);

        paymentServiceClient.sendPayment(
                new PaymentRequest(donationDto.getPaymentNumber(), donationDto.getAmount(), donationDto.getCurrency()));

        Donation donation = donationMapper.toEntity(donationDto);
        donationRepository.save(donation);
        return donationMapper.toDto(donation);
    }
}
