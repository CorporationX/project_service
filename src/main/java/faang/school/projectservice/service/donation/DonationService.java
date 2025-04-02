package faang.school.projectservice.service.donation;

import faang.school.projectservice.dto.donation.DonationCreateRequest;
import faang.school.projectservice.dto.donation.DonationResponse;

public interface DonationService {

    DonationResponse createDonation(DonationCreateRequest donationCreateRequest);
}
