package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/donations")
@Tag(name = "Donation controller", description = "Provide several operations with donations")
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    @Operation(summary = "Sending donation to project", description = "Provides ability to support project with donation")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public DonationDto sendDonation(@RequestBody @Parameter(description = "Donation description", required = true) DonationDto donationDto) {
        return donationService.sendDonation(donationDto);
    }

    @GetMapping(value = "/{userId}/{donationId}")
    @Operation(summary = "Getting donation", description = "Provides ability to get donation by id and user id")
    @ApiResponse(responseCode = "404", description = "Project doesn't exist")
    public DonationDto getDonationByIdAndUserId(@PathVariable @Parameter(description = "Donation id", required = true) long donationId,
                                                @PathVariable @Parameter(description = "Donator id", required = true) long userId) {
        return donationService.getDonationByIdAndUserId(donationId, userId);
    }

    @GetMapping(value = "/{userId}")
    @Operation(summary = "Getting all donations", description = "Provides ability to get all users donations")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "User doesn't exist"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public List<DonationDto> getAllDonationsByUserId(
            @PathVariable @Parameter(description = "Donator id", required = true) long userId,
            @RequestBody @Parameter(description = "Donation filters", required = true) DonationFilterDto donationFilterDto) {
        return donationService.getAllDonationsByUserId(userId, donationFilterDto);
    }
}
