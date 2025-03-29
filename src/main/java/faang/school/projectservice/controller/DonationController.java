package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.donation.DonationViewDto;
import faang.school.projectservice.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для работы с донатами
 * <p>
 * Этот класс отвечает за обработку запросов, связанных с донатами, их создание и получение
 * </p>
 *
 * <p><b>Основные функции:</b>
 * <ul>
 *     <li>{@link #sendDonation(DonationCreateDto, long)} Создание и отправка нового доната}</li>
 *     <li>{@link #getDonationByIdForUser(long, long)} Поиск доната по идентификатору для конкретного пользователя}</li>
 *     <li>{@link #getUserDonations(DonationFilterDto, long)} Поиск донатов пользователя с применением фильтра}</li>
 * </ul>
 * </ul>
 * </p>
 * @author juzu400
 * @see DonationCreateDto
 * @see DonationViewDto
 * @see DonationService
 * @see DonationFilterDto
 * @see UserContext
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/donations")
public class DonationController {
    private final DonationService donationService;
    private final UserContext userContext;

    @ModelAttribute("userId")
    private long getUserId() {
        return userContext.getUserId();
    }

    /**
     * Создание и отправка нового доната
     *
     * @param donation DTO для создания доната
     * @return созданный донат
     */
    @PostMapping("/send")
    public DonationViewDto sendDonation(@Valid @RequestBody DonationCreateDto donation,
                                        @ModelAttribute("userId") long userId) {
        log.info("Началось отправление доната");
        return donationService.sendDonation(donation, userId);
    }

    /**
     * Получение доната по идентификатору для конкретного пользователя
     *
     * @param donationId Идентификатор доната
     * @return донат
     */
    @GetMapping("/{donationId}")
    public DonationViewDto getDonationByIdForUser(@PathVariable long donationId,
                                                  @ModelAttribute("userId") long userId) {
        log.info("Получение доната по id для пользователя");
        return donationService.getDonationByIdForUser(donationId, userId);
    }

    /**
     * Получение списка донатов пользователя с применением фильтра
     *
     * @param filter фильтр для поиска донатов
     * @return список донатов
     */
    @GetMapping()
    public List<DonationViewDto> getUserDonations(@Valid @ModelAttribute DonationFilterDto filter,
                                                  @ModelAttribute("userId") long userId) {
        log.info("Получение донатов пользователя");
        return donationService.getUserDonations(userId, filter);
    }
}
