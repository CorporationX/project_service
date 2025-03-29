package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.donation.DonationViewDto;
import faang.school.projectservice.service.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Donation Controller", description = "Контроллер для работы с донатами")
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

    @Operation(
            summary = "Отправка доната",
            description = "Метод для отправки доната пользователем"
    )
    @PostMapping("/send")
    public DonationViewDto sendDonation(
            @Valid
            @RequestBody
            @Parameter(description = "DTO с данными для создания доната", required = true)
            DonationCreateDto donation,
            @ModelAttribute("userId")
            @RequestHeader("x-user-id")
            @Parameter(description = "идентификатор пользователя")
            long userId) {
        log.info("Началось отправление доната");
        return donationService.sendDonation(donation, userId);
    }

    @Operation(
            summary = "Получение доната по id",
            description = "Метод для получения доната по id для конкретного пользователя"
    )
    @GetMapping("/{donationId}")
    public DonationViewDto getDonationByIdForUser(
            @PathVariable
            @Parameter(description = "ID доната", required = true)
            long donationId,
            @ModelAttribute("userId")
            @RequestHeader("x-user-id")
            @Parameter(description = "ID пользователя", required = true)
            long userId) {
        log.info("Получение доната по id для пользователя");
        return donationService.getDonationByIdForUser(donationId, userId);
    }

    @Operation(
            summary = "Получение всех донатов пользователя",
            description = "Метод для получения всех донатов пользователя с фильтрацией"
    )
    @GetMapping()
    public List<DonationViewDto> getUserDonations(
            @Valid
            @ModelAttribute
            @Parameter(description = "DTO с фильтром для получения донатов")
            DonationFilterDto filter,
            @ModelAttribute("userId")
            @RequestHeader("x-user-id")
            @Parameter(description = "ID пользователя", required = true)
            long userId) {
        log.info("Получение донатов пользователя");
        return donationService.getUserDonations(userId, filter);
    }
}
