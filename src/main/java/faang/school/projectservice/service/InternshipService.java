package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFiltersDto;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class InternshipService {
    private final InternshipValidator validator;
    private final InternshipMapper internshipMapper;
    private final InternshipRepository internshipRepository;
    private final TeamMemberService teamMemberService;
    private final List<InternshipFilter> filters;

    public InternshipDto create(InternshipDto internshipDto) {
        validator.validateForCreation(internshipDto);

        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setStatus(InternshipStatus.IN_PROGRESS);

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    public InternshipDto update(InternshipDto internshipDto) {

        Internship internshipToBeUpdated = getInternship(internshipDto.getId());
        if (internshipToBeUpdated.getStatus().equals(InternshipStatus.COMPLETED))
            throw new Exception("Cannot be updating");

        validator.validateForUpdate(internshipDto, internshipToBeUpdated);

        internshipMapper.updateEntity(internshipDto, internshipToBeUpdated);

        if (internshipToBeUpdated.getStatus().equals(InternshipStatus.COMPLETED)) {
            teamMemberService.changeRoleForInterns(internshipToBeUpdated);
        }

        return internshipMapper.toDto(internshipRepository.save(internshipToBeUpdated));
    }

    public List<InternshipDto> getAllInternshipByFilters(InternshipFiltersDto internshipFiltersDto) {
        return null;
    }

    public List<InternshipDto> getAllInternshipById() {
        return internshipMapper.toDtoList(internshipRepository.findAll());
    }

    public InternshipDto getInternshipById(Long id) {
        return internshipMapper.toDto(getInternship(id));
    }


    public Internship getInternship(Long id) {
        //todo: exception
        return internshipRepository.findById(id).orElseThrow();
    }

}
