package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationToSendDto;
import faang.school.projectservice.dto.donation.filter.DonationFilterDto;
import faang.school.projectservice.service.donation.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import faang.school.projectservice.config.context.UserContext;

import java.util.List;

@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;
    private final UserContext userContext;

    @Operation(summary = "Create a new donation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Donation created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DonationDto.class))})
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationDto sendDonation(@RequestBody @Valid DonationToSendDto donationDto) {
        long userId = userContext.getUserId();
        return donationService.sendDonation(userId, donationDto);
    }

    @Operation(summary = "Get donation by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Finds donation by donation id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DonationDto.class))})
    })
    @GetMapping("/{donationId}")
    @ResponseStatus(HttpStatus.OK)
    public DonationDto getDonationById(@PathVariable("donationId") long donationId) {
        return donationService.getDonationById(donationId);
    }

    @Operation(summary = "Get all donations by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all user donations",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DonationDto.class))})
    })
    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<DonationDto> getAllDonationsByUserId(@PathVariable("userId") long userId,
                                                     @ParameterObject @RequestBody(required = false) DonationFilterDto filter) {
        return donationService.getAllDonationsByUserId(userId, filter);
    }
}
