package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.internship.InternshipDtoMapper;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internships")
@RequiredArgsConstructor
public class InternshipController {

    @PostMapping
    public InternshipDto addInternship(@Valid @RequestBody CreateInternshipDto createInternshipDto,
                                       InternshipService internshipService,
                                       InternshipMapper internshipMapper,
                                       InternshipDtoMapper internshipDtoMapper
    ) {
        return internshipService.createInternship(createInternshipDto);
    }
}