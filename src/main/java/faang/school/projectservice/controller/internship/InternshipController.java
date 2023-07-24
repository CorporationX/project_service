package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.ResponseInternshipDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public ResponseInternshipDto create(CreateInternshipDto dto) {
        return internshipService.create(dto);
    }

    public ResponseInternshipDto update(UpdateInternshipDto dto) {
        return internshipService.update(dto);
    }

    public List<ResponseInternshipDto> getAllByFilter(InternshipFilterDto internshipFilterDto) {
        return internshipService.findByFilter(internshipFilterDto);
    }

    public List<ResponseInternshipDto> getAll() {
        return internshipService.findAll();
    }

    public ResponseInternshipDto getById(Long id) {
        return internshipService.findById(id);
    }
}
