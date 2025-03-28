package faang.school.projectservice.validation.donation;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Класс для валидации создания нового доната.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DonationValidator {
    private final CampaignRepository campaignRepository;
    private final UserServiceClient userServiceClient;

    /**
     * Проверяет корректность данных для создания доната
     *
     * @param donation DTO с данными для создания доната
     * @param userId Идентификатор пользователя, который отправляет донат
     * @throws DataValidationException если донат не прошёл валидацию
     */
    public void validateDonation(DonationCreateDto donation, long userId) {
        validateAmount(donation.getAmount());
        validateCampaignExist(donation.getCampaignId());
        validateUserExist(userId);
    }

    /**
     * Проверка, что сумма доната больше нуля
     *
     * @param amount сумма доната
     * @throws DataValidationException если сумма доната меньше или равна нулю
     */
    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Сумма доната не может быть отрицательной или равной нулю");
            throw new DataValidationException("amount must be greater than zero");
        }
    }

    /**
     * Проверка, что кампания существует
     *
     * @param campaignId идентификатор кампании
     * @throws DataValidationException если кампания не найдена
     */
    private void validateCampaignExist(Long campaignId) {
        if (!campaignRepository.existsById(campaignId)) {
            log.error("Кампания с id {} не найдена", campaignId);
            throw new DataValidationException("campaign with id " + campaignId + " not found");
        }
    }

    /**
     * Проверка, что пользователь существует
     *
     * @param userId идентификатор пользователя
     * @throws DataValidationException если пользователь не найден
     */
    private void validateUserExist(Long userId) {
        UserDto user = userServiceClient.getUser(userId);
        if (user == null) {
            log.error("Пользователь с id {} не найден", userId);
            throw new DataValidationException("user with id " + userId + " not found");
        }
    }
}
