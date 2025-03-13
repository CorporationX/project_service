package faang.school.projectservice.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        if (internshipDto.getInternsId() == null || internshipDto.getInternsId().isEmpty()) {
            throw new NotFoundException("Список стажеров не может быть пустым.");
        }
        return internshipService.createInternship(internshipDto);
    }

    public InternshipDto getInternshipById(Long id) {
        return internshipService.getInternshipById(id);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto) {
        if (internshipDto == null) {
            throw new NotFoundException("internshipDto is null");
        }
        return internshipService.updateInternship(internshipDto);
    }

    public List<InternshipDto> findAllInternships() {
        return internshipService.getAllInternships();
    }

    public List<InternshipDto> findInternshipsByFilter(InternshipFilterDto internshipFilterDto) {
        if (internshipFilterDto == null) {
            throw new NotFoundException("internshipFilterDto is null");
        }
        return internshipService.getInternshipsFiltered(internshipFilterDto);
    }
}
