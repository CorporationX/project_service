package faang.school.projectservice.service.donation;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.DonationFilterStrategy;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.DonationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
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
        PaymentRequest paymentRequest = new PaymentRequest(
                donationDto.getPaymentNumber(),
                donationDto.getAmount(),
                donationDto.getCurrency(),
                donationDto.getCurrency()
        );

        PaymentResponse paymentResponse = paymentServiceClient.sendPayment(paymentRequest);

        if (!Objects.equals(paymentResponse.status(), "SUCCESS")) {
            log.warn("Attempt to send donation failed:\nDonationDto:\n{}\nPaymentRequest:\n{}\nPaymentResponse:\n{}",
                    donationDto, paymentRequest, paymentResponse);
            throw new IllegalArgumentException(
                    "Payment with payment number: (%s) hasn't gone through".formatted(donationDto.getPaymentNumber())
            );
        }

        Donation donation = donationMapper.toEntity(donationDto);
        donation = donationRepository.save(donation);

        return donationMapper.toDto(donation);
    }

    @Override
    public DonationDto getDonationByIdAndUserId(long donationId, long userId) {
        Donation donation = donationRepository.findByIdAndUserId(donationId, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Either donation with Id (%s) or user with Id (%s) were not found"
                                .formatted(donationId, userId)));

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
                .sorted(Comparator.comparing(DonationDto::getDonationTime))
                .toList();
    }
}
