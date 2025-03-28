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
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.validation.donation.DonationValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Сервис для управления донатами.
 * <p>
 * Этот сервис предоставляет методы для создания и получения донатов.
 * </p>
 * <p>
 * Основные функции:
 * <ul>
 *     <li>{@link #sendDonation(DonationCreateDto, long)} Создание и отправка нового доната} с проверкой валидности данных.</li>
 *     <li>{@link #getDonationByIdForUser(long, long)} Поиск доната по идентификатору для конкретного пользователя}</li>
 *     <li>{@link #getUserDonations(long, DonationFilterDto)} Поиск донатов пользователя с применением фильтра}</li>
 * </ul>
 * </p>
 * @author juzu400
 * @see DonationCreateDto
 * @see DonationViewDto
 * @see DonationFilterDto
 * @see DonationFilter
 * @see Donation
 * @see PaymentServiceClient
 * @see DonationValidator
 * @see DonationMapper
 */
@Service
@RequiredArgsConstructor
public class DonationService {
    private final DonationValidator validator;
    private final DonationMapper donationMapper;
    private final DonationRepository donationRepository;
    private final List<DonationFilter> donationFilter;
    private final PaymentServiceClient paymentService;
    private final CampaignRepository campaignRepository;

    /**
     * Создание и отправка нового доната.
     *
     * @param donationDto DTO для создания доната
     * @param userId Идентификатор пользователя
     * @return созданный донат
     */
    public DonationViewDto sendDonation(@NotNull DonationCreateDto donationDto, long userId) {
        validator.validateDonation(donationDto, userId);
        Campaign campaign = campaignRepository.findById(donationDto.getCampaignId()).orElseThrow();

        Donation donation = donationMapper.toEntity(donationDto);
        donation.setCampaign(campaign);
        donation.setUserId(userId);

        sendPayment(donation);
        donationRepository.save(donation);
        return donationMapper.toDto(donation);
    }

    /**
     * Поиск доната по идентификатору для конкретного пользователя.
     *
     * @param donationId Идентификатор доната
     * @param userId Идентификатор пользователя
     * @return найденный донат
     */
    public DonationViewDto getDonationByIdForUser(long donationId, long userId) {
        Donation donation = donationRepository.findByIdAndUserId(donationId, userId)
                .orElseThrow(() ->
                        new NotFoundException("donation with id " + donationId + "not found for user " + userId));
        return donationMapper.toDto(donation);
    }

    /**
     * Поиск донатов пользователя с применением фильтра.
     *
     * @param userId Идентификатор пользователя
     * @param filter Фильтр для донатов
     * @return список донатов пользователя
     */
    public List<DonationViewDto> getUserDonations(long userId, @NotNull DonationFilterDto filter) {
        var donations = donationRepository.findAllByUserId(userId);
        return applyFilters(donations, filter);
    }

    /**
     * Отправка платежа.
     *
     * @param donation Донат
     */
    private void sendPayment(Donation donation) {
        PaymentRequest request = getPaymentRequest(donation);
        PaymentResponse paymentResponse = paymentService.sendPayment(request);
        if (paymentResponse == null) {
            throw new RuntimeException("Payment failed");
        }
    }

    /**
     * Получение запроса на платеж.
     *
     * @param donation Донат
     * @return запрос на платеж
     */
    private PaymentRequest getPaymentRequest(Donation donation) {
        return new PaymentRequest(
                donation.getPaymentNumber(),
                donation.getAmount(),
                donation.getCurrency(),
                donation.getCampaign().getCurrency()
        );
    }

    /**
     * Применение фильтров.
     *
     * @param donations Список донатов
     * @param filter Фильтр для донатов
     * @return список отфильтрованных донатов
     */
    private List<DonationViewDto> applyFilters(List<Donation> donations, DonationFilterDto filter) {
        Stream<Donation> donationStream = donations.stream();
        for (DonationFilter donationFilter : donationFilter) {
            if (donationFilter.isApplicable(filter)) {
                donationStream = donationFilter.apply(donations.stream(), filter);
            }
        }
        return donationStream
                .sorted(Comparator.comparing(Donation::getDonationTime).reversed())
                .map(donationMapper::toDto)
                .toList();
    }
}
