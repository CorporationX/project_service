package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.donation.CreateDonationDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.donation.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    public ResponseEntity<DonationDto> send(@RequestBody @Valid CreateDonationDto createDonationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donationService.sendDonation(createDonationDto));
    }

    @GetMapping
    public ResponseEntity<List<DonationDto>> getDonationsByUserId(@RequestParam("userId") long userId,
                                                                  @ModelAttribute @Valid
                                                                  DonationFilterDto donationFilterDto) {
        return ResponseEntity.ok(donationService.getDonationsByUserId(userId,
                donationFilterDto));
    }

    @GetMapping("{id}")
    public ResponseEntity<DonationDto> getDonationByIdAndUserId(@PathVariable long id,
                                                                @RequestHeader("x-user-id") long userId ) {
        return ResponseEntity.ok(donationService.getDonationByIdAndUserId(id, userId));
    }
}