package faang.school.projectservice.controller;

import faang.school.projectservice.model.dto.MeetDto;
import faang.school.projectservice.model.dto.MeetFilterDto;
import faang.school.projectservice.service.MeetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meets")
@Tag(name = "Team meetings Management", description = "API for managing team meetings")
public class MeetController {

    private final MeetService meetService;

    @PostMapping()
    @Operation(summary = "Create a meet", description = "Create a new meet")
    public MeetDto create(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Valid @RequestBody MeetDto meetDto) throws GeneralSecurityException, IOException {
        return meetService.create(meetDto);
    }

    @PutMapping("/{meetId}")
    @Operation(summary = "Update meet", description = "Update an existing meet")
    public MeetDto update(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @PathVariable @NotNull long meetId,
            @Valid @RequestBody MeetDto meetDto) throws GeneralSecurityException, IOException {
        return meetService.update(meetId, meetDto);
    }

    @DeleteMapping("/{meetId}")
    @Operation(summary = "Delete a meet", description = "Delete a meet")
    public void delete(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "ID of the meet", required = true)
            @PathVariable Long meetId) throws GeneralSecurityException, IOException {
        meetService.delete(meetId);
    }

    @PostMapping("/filter")
    @Operation(summary = "Get meets by filter", description = "Retrieve meets by filter")
    public List<MeetDto> getMeetsByFilter(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "Filter for meets", required = true)
            @RequestBody MeetFilterDto filterDto) {
        return meetService.getByFilter(filterDto);
    }

    @GetMapping("")
    @Operation(summary = "Get all meets with pagination",
            description = "Retrieves a list of all meets with pagination support using limit and offset.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of meets"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Page<MeetDto> getAllMeets(@Parameter(description = "ID of the user", required = true)
                                     @RequestHeader("x-user-id") String userId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return meetService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an meet by ID", description = "Retrieve an meet by its ID")
    public MeetDto getMeetById(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") String userId,
            @Parameter(description = "ID of the meet", required = true)
            @PathVariable Long id) {
        return meetService.getById(id);
    }
}
