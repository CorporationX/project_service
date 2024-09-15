package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternshipServiceImpl implements InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;

    @Override
    public InternshipDto createInternship(InternshipDto internshipDto) {
        var internship = internshipMapper.toEntity(internshipDto);
        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    @Override
    public List<InternshipDto> getAllInternships() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    @Override
    public InternshipDto updateInternship(long id, InternshipDto internshipDto) {
        return null;
    }

    @Override
    public List<InternshipDto> getAllInternshipsByFilter(InternshipFilter filter) {
        return List.of();
    }

    @Override
    public InternshipDto getInternshipById(long id) {
        return null;
    }
}
