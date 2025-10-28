package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InternshipService {
    public InternshipDto createInternship(CreateInternshipDto createInternshipDto) {
        InternshipValidator.validateInternshipLength(createInternshipDto);

    }
}