package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.service.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/donations")
@Validated
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Донаты", description = "Донаты на фандрайзинг проекта")
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    @Operation(summary = "Создание доната")
    public DonationDto createDonation(@Valid @RequestBody DonationDto donationDto) {
        return donationService.createDonation(donationDto);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получение доната пользователя по id доната")
    public DonationDto getDonationById(@PathVariable Long userId, @RequestParam Long donationId) {
        return donationService.getDonationById(userId, donationId);
    }

    @GetMapping("/{userId}/all")
    @Operation(summary = "Получение списка донатов пользователя")
    public List<DonationDto> getAllDonations(@PathVariable Long userId) {
        return donationService.getAllDonations(userId);
    }

    @GetMapping("/{userId}/filter")
    @Operation(summary = "Получение списка донатов пользователя с фильтром")
    public List<DonationDto> getAllDonationsWithFilter(@Valid @RequestBody DonationFilter donationFilter) {
        return donationService.getAllDonationsWithFilter(donationFilter);
    }
}
