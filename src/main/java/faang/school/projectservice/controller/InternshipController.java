package faang.school.projectservice.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        if (internshipDto.getInternsId() == null || internshipDto.getInternsId().isEmpty()) {
            throw new NotFoundException("The list of interns cannot be empty.");
        }
        return internshipService.createInternship(internshipDto);
    }

    public InternshipDto getInternshipById(Long id) {
        return internshipService.getInternshipById(id);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto) {
        Objects.requireNonNull(internshipDto, "internshipDto is null");
        return internshipService.updateInternship(internshipDto);
    }

    public List<InternshipDto> findAllInternships() {
        return internshipService.getAllInternships();
    }

    public List<InternshipDto> findInternshipsByFilter(InternshipFilterDto internshipFilterDto) {
        Objects.requireNonNull(internshipFilterDto, "internshipFilterDto is null");
        return internshipService.getInternshipsFiltered(internshipFilterDto);
    }
}
