package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.common.PageResponse;
import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internships")
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    @PostMapping
    public InternshipDto createInternship(@Valid @RequestBody CreateInternshipDto createInternshipDto) {
        return internshipService.createInternship(createInternshipDto);
    }

    @PatchMapping("/{internshipId}")
    public InternshipDto updateInternship(@PathVariable long internshipId,
                                                          @Valid @RequestBody UpdateInternshipDto updateInternshipDto) {
        return internshipService.updateInternship(internshipId, updateInternshipDto);
    }

    @PostMapping("/search")
    public PageResponse<InternshipDto> getAllWithFilter(@RequestBody InternshipFilterDto internshipFilterDto,
                                                        @PageableDefault Pageable pageable) {
        return internshipService.getInternshipsByFiler(internshipFilterDto, pageable);
    }

    @GetMapping("/{internshipId}")
    public InternshipDto getById(@PathVariable long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }
}