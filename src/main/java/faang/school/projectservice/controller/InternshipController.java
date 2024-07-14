package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipDto create(InternshipDto internshipDto) {
        return internshipService.create(internshipDto);
    }
}
