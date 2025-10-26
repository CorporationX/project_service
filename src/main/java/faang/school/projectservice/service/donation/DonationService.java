package faang.school.projectservice.service.donation;

import faang.school.projectservice.dto.donation.CreateDonationDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;

import java.util.List;

public interface DonationService {
    DonationDto sendDonation(CreateDonationDto createDonationDto);

    List<DonationDto> getDonationsByUserId(long userId, DonationFilterDto donationFilterDto);

    DonationDto getDonationByIdAndUserId(long donationId, long userId);
}