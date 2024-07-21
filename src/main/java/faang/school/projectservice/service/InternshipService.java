package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFiltersDto;
import faang.school.projectservice.dto.client.InternshipToCreateDto;
import faang.school.projectservice.dto.client.InternshipToUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.exception.InternshipError.*;

@Service
@RequiredArgsConstructor
@Transactional
public class InternshipService {
    private final InternshipValidator validator;
    private final InternshipMapper internshipMapper;
    private final InternshipRepository internshipRepository;
    private final TeamMemberService teamMemberService;
    private final List<InternshipFilter> filters;
    private final TeamMemberRepository teamMemberRepository;

    public InternshipDto create(long userId, InternshipToCreateDto internshipDto) {
        validator.validateForCreation(userId, internshipDto);

        Internship internship = internshipMapper.toEntity(internshipDto);

        internship.setStatus(InternshipStatus.IN_PROGRESS);
        internship.setCreatedBy(userId);

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    public InternshipDto update(long userId, InternshipToUpdateDto internshipDto) {
        Internship internshipToBeUpdated = getInternship(internshipDto.getId());

        if (internshipToBeUpdated.getStatus().equals(InternshipStatus.COMPLETED))
            throw new DataValidationException(CANNOT_UPDATING_EXCEPTION);


        validator.validateForUpdate(userId, internshipDto, internshipToBeUpdated);
        internshipMapper.updateEntity(internshipDto, internshipToBeUpdated);

        internshipToBeUpdated.setUpdatedBy(userId);

        if (internshipToBeUpdated.getStatus().equals(InternshipStatus.COMPLETED)) {
            teamMemberService.changeRoleForInternsAndDeleteFiredInterns(internshipToBeUpdated);
        }
        updateMentor(internshipDto, internshipToBeUpdated);
        //добаление участников
        addMembers(internshipDto, internshipToBeUpdated);

        //изменение даты
        updateDate(internshipDto, internshipToBeUpdated);

        return internshipMapper.toDto(internshipRepository.save(internshipToBeUpdated));
    }

    private void updateMentor(InternshipToUpdateDto internshipDto, Internship internshipToBeUpdated) {
        Long mentorId = internshipDto.getMentorId();
        if (internshipDto.getMentorId() != null) {
            validator.validateMentorExists(mentorId);
            internshipToBeUpdated.setMentorId(teamMemberRepository.findById(mentorId));
        }
    }

    @Transactional(readOnly = true)
    public List<InternshipDto> getAllInternshipByFilters(Long projectId, InternshipFiltersDto internshipFiltersDto) {
        List<Internship> internshipProjects = internshipRepository.findAll().stream()
                .filter(internship -> internship.getProject().getId().equals(projectId))
                .toList();

        Stream<Internship> internshipStream = internshipProjects.stream();

        List<Internship> allInternshipByFilters = filters.stream()
                .filter(filter -> filter.isApplicable(internshipFiltersDto))
                .reduce(internshipStream, (acc, filter) -> filter.apply(acc, internshipFiltersDto), Stream::concat)
                .toList();
        return internshipMapper.toDtoList(allInternshipByFilters);
    }

    @Transactional(readOnly = true)
    public List<InternshipDto> getAllInternship() {
        return internshipMapper.toDtoList(internshipRepository.findAll());
    }

    @Transactional(readOnly = true)
    public InternshipDto getInternshipById(Long id) {
        return internshipMapper.toDto(getInternship(id));
    }

    @Transactional(readOnly = true)
    public Internship getInternship(Long id) {
        return internshipRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(NON_EXISTING_INTERNSHIP_EXCEPTION));
    }

    private void addMembers(InternshipToUpdateDto internshipDto, Internship internshipToBeUpdated) {
        List<Long> internsFromDto = internshipDto.getInternsId();
        if (internsFromDto != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(internshipToBeUpdated.getStartDate())) {
                List<Long> internsOfInternship = internshipMapper.internsById(internshipToBeUpdated.getInterns());

                internsFromDto.removeAll(internsOfInternship);

                if (internsFromDto.isEmpty()) {
                    throw new DataValidationException(CANNOT_TO_ADDED_INTERNS);
                }

                List<TeamMember> internsToBeAdd = internsFromDto.stream()
                        .map(teamMemberRepository::findById).toList();

                internshipToBeUpdated.getInterns().addAll(internsToBeAdd);

                internshipToBeUpdated.setInterns(internshipToBeUpdated.getInterns());

            } else
                throw new DataValidationException(CANNOT_TO_ADD_INTERNS_AFTER_START_EXCEPTION);
        }
    }

    private void updateDate(InternshipToUpdateDto internshipDto, Internship internship) {
        if (internshipDto.getStartDate() == null && internshipDto.getEndDate() != null) {
            LocalDateTime startInternshipFromDb = internship.getStartDate();
            LocalDateTime endDate = internshipDto.getEndDate();

            validator.validateInternshipDuration(startInternshipFromDb, endDate);

            internship.setEndDate(endDate);
        } else if (internshipDto.getStartDate() != null && internshipDto.getEndDate() == null) {
            LocalDateTime startDate = internshipDto.getStartDate();
            LocalDateTime endDateFromDb = internship.getEndDate();

            validator.validateInternshipDuration(startDate, endDateFromDb);

            internship.setStartDate(startDate);
        }
    }
}
