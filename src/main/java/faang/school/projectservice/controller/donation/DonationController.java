package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.filter.DonationFilterDto;
import faang.school.projectservice.service.donation.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Donation")
@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @Operation(summary = "Create a new donation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Donation created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DonationDto.class))})
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationDto sendDonation(
            @Parameter(description = "User ID", required = true) @RequestHeader("x-user-id") long userId,
            @RequestBody @Valid DonationCreateDto donationDto) {
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
