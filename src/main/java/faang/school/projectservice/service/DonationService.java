package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.payment.PaymentRequest;
import faang.school.projectservice.dto.payment.PaymentResponse;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.PaymentFailedException;
import faang.school.projectservice.filter.DonationFilter;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationService {
    private final DonationRepository donationRepository;
    private final DonationMapper donationMapper;
    private final List<DonationFilter> donationFilters;
    private final PaymentServiceClient paymentServiceClient;
    private final UserContext userContext;

    public DonationDto sendDonation(DonationDto donationDto) {
        log.info("Отправка доната: {}", donationDto);

        PaymentRequest paymentRequest = new PaymentRequest(
                donationDto.getPaymentNumber(),
                donationDto.getAmount(),
                donationDto.getCurrency()
        );

        try {
            log.info("Запрос в сервис платежа: {}", paymentRequest);
            PaymentResponse response = paymentServiceClient.sendPayment(paymentRequest);
            log.info("Ответ от платежного сервиса: {}", response.status());

            Donation donation = donationMapper.toEntity(donationDto);
            Donation savedDonation = donationRepository.save(donation);
            log.info("Донат сохранён: {}", savedDonation);

            return donationMapper.toDto(savedDonation);
        } catch (Exception e) {
            throw new PaymentFailedException("Ошибка при обработке платежа", e);
        }
    }

    public DonationDto getDonationByUserId(Long donationId, Long userId) {
        validateId(donationId);
        validateId(userId);
        Donation donation = donationRepository.findByIdAndUserId(donationId, userId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Донат c id %d не найден у юзера c id %d", donationId, userId))
        );
        return donationMapper.toDto(donation);
    }

    public List<DonationDto> getUserDonationsByFilters(DonationFilterDto filter, Long userId) {
        Stream<Donation> donations = donationRepository.findAllByUserId(userId).stream();

        return donationFilters.stream()
                .filter(donationFilter -> donationFilter.isApplicable(filter))
                .reduce(donations,
                        (donationStream, donationFilter)
                                -> donationFilter.apply(donationStream, filter),
                        Stream::concat)
                .sorted(Comparator.comparing(Donation::getDonationTime,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(donationMapper::toDto)
                .toList();
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new DataValidationException("ID не может быть null");
        }
    }
}
