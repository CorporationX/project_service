package faang.school.projectservice.service.donation;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.DonationFilterStrategy;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.DonationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationMapper donationMapper;
    private final DonationRepository donationRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final List<DonationFilterStrategy> donationFilterStrategies;
    private final UserServiceClient userServiceClient;

    @Override
    public DonationDto sendDonation(DonationDto donationDto) {
        Donation donation = donationMapper.toEntity(donationDto);

        PaymentRequest paymentRequest = new PaymentRequest(
                donation.getPaymentNumber(),
                donation.getAmount(),
                donation.getCurrency(),
                donation.getCurrency()
        );

        if (!Objects.equals(paymentServiceClient.sendPayment(paymentRequest).status(), "SUCCESS")) {
            throw new IllegalArgumentException("Payment hasn't gone through");
        }

        donation = donationRepository.save(donation);

        return donationMapper.toDto(donation);
    }

    @Override
    public DonationDto getDonationByIdAndUserId(long donationId, long userId) {
        Donation donation = donationRepository.findByIdAndUserId(donationId, userId).orElseThrow(
                () -> new EntityNotFoundException("Either donation with Id (%s) or user with Id (%s) were not found".formatted(donationId, userId))
        );

        return donationMapper.toDto(donation);
    }

    @Override
    public List<DonationDto> getAllDonationsByUserId(long userId, DonationFilterDto donationFilterDto) {
        userServiceClient.getUser(userId);

        List<Donation> donations = donationRepository.findAllByUserId(userId);
        if (donations.isEmpty()) return List.of();

        Stream<Donation> donationStream = donations.stream();
        for (DonationFilterStrategy filter : donationFilterStrategies) {
            if (filter.isApplicable(donationFilterDto)) {
                donationStream = filter.apply(donationStream, donationFilterDto);
            }
        }

        return donationStream
                .map(donationMapper::toDto)
                .sorted((d1, d2)
                        -> d1.getDonationTime().isBefore(d2.getDonationTime()) ? 1 : -1)
                .toList();
    }
}
