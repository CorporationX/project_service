package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.service.donation.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Donation management", description = "Operations related to donations to campaigns")
@RequiredArgsConstructor
@Validated
@RestController
public class DonationController {

    private final DonationService donationService;
    private final DonationMapper donationMapper;

    @Operation(
            summary = "Create a new donation",
            description = "Accepts donation details and returns the created donation data.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Donation request payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DonationDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Donation successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/donation/create")
    public ResponseEntity<DonationDto> createDonation(@Valid @RequestBody DonationDto donationDtoRequest) {

        Donation donationRequest = donationMapper.toEntity(donationDtoRequest);

        Donation donationResponse = donationService.createDonation(donationRequest);

        DonationDto donationDtoResponse = donationMapper.toDto(donationResponse);

        return ResponseEntity.ok(donationDtoResponse);
    }

    @Operation(
            summary = "Get a donation by ID",
            description = "Retrieves a donation based on the provided donation ID.",
            parameters = {
                    @Parameter(
                            name = "donationId",
                            description = "ID of the donation to retrieve",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Donation retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Donation not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/donation/{donationId}")
    public ResponseEntity<DonationDto> getDonationById(@Parameter(description = "Id of the donation you want to retrieve")
                                                           @PathVariable long donationId) {

        Donation donationResponse = donationService.getDonationById(donationId);

        DonationDto donationDtoResponse = donationMapper.toDto(donationResponse);

        return ResponseEntity.ok(donationDtoResponse);
    }

    @Operation(
            summary = "Get all donations for a user",
            description = "Retrieves a list of all donations made by a user, optionally filtered by the given criteria.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Optional filters for retrieving donations",
                    required = false,
                    content = @Content(
                            schema = @Schema(implementation = DonationFilterDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of user donations retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request format"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/donations")
    public ResponseEntity<List<DonationDto>> getAllUserDonations(
            @RequestBody(required = false) DonationFilterDto dtoFilters) {

        List<Donation> donationsResponse = donationService.getAllUserDonations(dtoFilters);

        List<DonationDto> donationDtosResponse = donationMapper.toDto(donationsResponse);

        return ResponseEntity.ok(donationDtosResponse);
    }
}
