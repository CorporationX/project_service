package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {
    @Override
    public void createInternship(InternshipDto internshipDto) {

    }

    @Override
    public InternshipDto updateInternship(Long id) {
        return null;
    }

    @Override
    public List<InternshipDto> getInternshipsWithFilters(InternshipFilterDto filters) {
        return List.of();
    }

    @Override
    public List<InternshipDto> getAllInternships() {
//        List<Internship> internships = InternshipRepository.
        return List.of();
    }

    @Override
    public InternshipDto getInternship(Long id) {
        return null;
    }
}
