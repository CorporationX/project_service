package faang.school.projectservice.service.impl.donation;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.dto.client.PaymentRequest;
import faang.school.projectservice.model.dto.donation.DonationDto;
import faang.school.projectservice.model.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.entity.Donation;
import faang.school.projectservice.model.filter.donation.DonationFilter;
import faang.school.projectservice.model.mapper.donation.DonationMapper;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.DonationService;
import faang.school.projectservice.validator.donation.DonationValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final DonationValidator donationValidator;
    private final PaymentServiceClient paymentServiceClient;
    private final DonationMapper donationMapper;
    private final UserContext userContext;
    private final List<DonationFilter> donationFilters;

    @Override
    @Transactional
    public DonationDto sendDonation(DonationDto donationDto) {
        donationValidator.validateSending(donationDto);
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .amount(donationDto.amount())
                .currency(donationDto.currency())
                .paymentNumber(donationDto.paymentNumber())
                .build();

        paymentServiceClient.sendPayment(paymentRequest);
        Donation donation = donationMapper.toEntity(donationDto);

        return donationMapper.toDto(donationRepository.save(donation));
    }

    @Override
    @Transactional(readOnly = true)
    public DonationDto getDonationById(long id) {
        long userId = userContext.getUserId();
        Donation donation = donationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Donation with user id %s and donation id %s not found"
                        .formatted(userId, id)));

        return donationMapper.toDto(donation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonationDto> getAllDonationsByUserId(long userId, DonationFilterDto filterDto) {
        Stream<Donation> donations = donationRepository.findAllByUserId(userId).stream();

        return donationFilters.stream()
                .filter(donationFilter -> donationFilter.isApplicable(filterDto))
                .reduce(donations, (stream, filter) -> filter.apply(stream, filterDto),
                        (newStream, oldStream) -> newStream)
                .sorted(Comparator.comparing(Donation::getDonationTime))
                .map(donationMapper::toDto)
                .toList();
    }
}
