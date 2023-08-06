package faang.school.projectservice.service;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;


    public List<InternshipDto> getAllInternships() {
        return internshipMapper.toListDto(internshipRepository.findAll());
    }

    public InternshipDto getInternshipBy(Long internshipId) {
        Optional<Internship> internshipOpt = internshipRepository.findById(internshipId);
        if (internshipOpt.isPresent()) {
            return internshipMapper.toDto(internshipOpt.get());
        } else {
            throw new DataValidationException("Internship not found with ID: " + internshipId);
        }
    }
}
