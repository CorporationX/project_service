package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.donation.DonationViewDto;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.validation.donation.DonationValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DonationService {
    private final DonationValidator validator;
    private final DonationMapper donationMapper;
    private final DonationRepository donationRepository;
    private final List<DonationFilter> donationFilter;
    private final PaymentServiceClient paymentService;

    public DonationViewDto sendDonation(@NotNull DonationCreateDto donationDto, long userId) {
        validator.validateDonation(donationDto, userId);
        Donation donation = donationMapper.toEntity(donationDto);
        sendPayment(donation);
        donationRepository.save(donation);
        return donationMapper.toDto(donation);
    }

    public DonationViewDto getDonationByIdForUser(long donationId, long userId) {
        Donation donation = donationRepository.findByIdAndUserId(donationId, userId)
                .orElseThrow(() ->
                        new NotFoundException("donation with id " + donationId + "not found for user " + userId));
        return donationMapper.toDto(donation);
    }

    public List<DonationViewDto> getUserDonations(long userId, @NotNull DonationFilterDto filter) {
        var donations = donationRepository.findAllByUserId(userId);
        return applyFilters(donations, filter);
    }

    private void sendPayment(Donation donation) {
        PaymentRequest request = getPaymentRequest(donation);
        PaymentResponse paymentResponse = paymentService.sendPayment(request);
        if (paymentResponse == null) {
            throw new RuntimeException("Payment failed");
        }
    }

    private PaymentRequest getPaymentRequest(Donation donation) {
        return new PaymentRequest(
                donation.getPaymentNumber(),
                donation.getAmount(),
                donation.getCurrency(),
                donation.getCampaign().getCurrency()
        );
    }

    private List<DonationViewDto> applyFilters(List<Donation> donations, DonationFilterDto filter) {
        Stream<Donation> donationStream = donations.stream();
        for (DonationFilter donationFilter : donationFilter) {
            if (donationFilter.isApplicable(filter)) {
                donationStream = donationFilter.apply(donations.stream(), filter);
            }
        }
        return donationStream
                .map(donationMapper::toDto)
                .toList();
    }
}
