package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.ResponseInternshipDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internship")
//http://localhost:8082/swagger-ui/index.html#/
@Tag(name = "Internship", description = "Internship management APIs")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping()
    public ResponseInternshipDto create(@RequestBody CreateInternshipDto dto) {
        return internshipService.create(dto);
    }

    @PatchMapping()
    public ResponseInternshipDto update(@RequestBody UpdateInternshipDto dto) {
        return internshipService.update(dto);
    }

    @PostMapping("/byFilter")
    public List<ResponseInternshipDto> getAllByFilter(@RequestBody InternshipFilterDto internshipFilterDto) {
        return internshipService.findByFilter(internshipFilterDto);
    }

    @GetMapping()
    public List<ResponseInternshipDto> getAll() {
        return internshipService.findAll();
    }

    @Operation(
            summary = "Retrieve a Internship by Id",
            description = "Get a Internship object by specifying its id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ResponseInternshipDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})})
    @GetMapping("/{id}")
    public ResponseInternshipDto getById(@PathVariable Long id) {
        if (id == 1) {
            return ResponseInternshipDto.builder().id(1L).name("Name").build();
        } else {
            return internshipService.findById(id);
        }
    }
}
