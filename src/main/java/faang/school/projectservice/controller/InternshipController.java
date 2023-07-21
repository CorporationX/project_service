package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    public InternshipDto internshipCreation(InternshipDto internshipDto) {
        if (!internshipValidation(internshipDto)) {
            throw new IllegalArgumentException("Validation parameters did not passed!");
        }
        return internshipService.internshipCreation(internshipDto);
    }

    public InternshipDto internshipUpdate(InternshipDto internshipDto) {
        return internshipService.internshipUpdate(internshipDto);
    }

    private boolean internshipValidation(InternshipDto internshipDto) {
        return internshipDto.getName() != null &&
                internshipDto.getStartDate() != null &&
                internshipDto.getEndDate() != null &&
                internshipDto.getDescription() != null &&
                internshipDto.getMentorId() != null &&
                internshipDto.getInternsId() != null &&
                internshipDto.getProjectId() != null &&
                internshipDto.getStatus() != null;
    }
}
