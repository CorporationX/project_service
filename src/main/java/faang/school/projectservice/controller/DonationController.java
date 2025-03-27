package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.donation.DonationViewDto;
import faang.school.projectservice.service.DonationService;
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

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/donations")
public class DonationController {
    private final DonationService donationService;
    private final UserContext userContext;

    @PostMapping("/send")
    public DonationViewDto sendDonation(@RequestBody DonationCreateDto donation) {
        long userId = userContext.getUserId();
        log.info("Началось отправление доната");
        return donationService.sendDonation(donation, userId);
    }

    @GetMapping("/{donationId}/{userId}")
    public DonationViewDto getDonationByIdForUser(@PathVariable long donationId, @PathVariable long userId) {
        log.info("Получение доната по id для пользователя");
        return donationService.getDonationByIdForUser(donationId, userId);
    }

    @GetMapping("/{userId}")
    public List<DonationViewDto> getUserDonations(@PathVariable long userId, @ModelAttribute DonationFilterDto filter) {
        log.info("Получение донатов пользователя");
        return donationService.getUserDonations(userId, filter);
    }
}
