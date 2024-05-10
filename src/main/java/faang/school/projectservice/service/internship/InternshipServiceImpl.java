package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internship.filter.InternshipFilterService;
import faang.school.projectservice.validator.internship.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.TeamRole.INTERN;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {
    private final InternshipRepository internshipRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipValidator validator;
    private final InternshipMapper mapper;
    private final InternshipFilterService internshipFilterService;

    @Override
    @Transactional
    // TODO нужно добавить проверки на то что стажировка относится к какому-то проекту
    // TODO добавить проверку на на то что стажировка длится не дольше трех месяцев
    // TODO проверить наличия ментора
    // TODO проверить, что нам есть кого стажировать
    public InternshipDto createInternship(InternshipDto internshipDto) {
        validator.validateInternshipExistence(internshipDto);

        Internship internship = mapper.toEntity(internshipDto);

        validator.validateInternshipNotStarted(internship);

        internshipRepository.save(internship);

        return mapper.toDto(internship);
    }

    @Override
    @Transactional
    public InternshipDto updateInternship(InternshipDto updatedInternshipDto) {

        Internship internship = getInternshipById(updatedInternshipDto.getId());
        Internship updatedInternship = mapper.toEntity(updatedInternshipDto);

        validator.validateInternshipNotStarted(internship);
        validator.validateInternshipNotCompleted(internship);
        validator.validateUpdatedInternshipDiffersByLast(internship, updatedInternship);

        internshipRepository.deleteById(internship.getId());
        internshipRepository.save(updatedInternship);

        return mapper.toDto(updatedInternship);

    }

    @Override
    @Transactional
    // TODO пишем ли мы в базу результат выполнения метода?
    public InternshipDto addNewIntern(long internshipId, long newInternId) {

        Internship internship = getInternshipById(internshipId);
        TeamMember newIntern = teamMemberRepository.findById(internshipId);

        validator.validateInternshipNotStarted(internship);
        validator.validateInternNotAlreadyInInternship(internship, newIntern);

        internship.getInterns().add(newIntern);

        return mapper.toDto(internship);
    }

    @Override
    @Transactional
    // TODO пишем ли мы в базу результат выполнения метода?
    public InternshipDto finishInternshipForIntern(long internshipId, long internId, TeamRole teamRole) {
        Internship internship = getInternshipById(internshipId);
        TeamMember intern = searchInternInInternship(internship, internId);

        validator.validateDateNotExpired(internship);
        validator.checkAllTasksDone(intern);

        changeInternRole(intern, teamRole);
        internship = updateInternsRoles(internshipId, intern);

        return mapper.toDto(internship);
    }

    @Override
    @Transactional
    // TODO пишем ли мы в базу результат выполнения метода?
    public InternshipDto removeInternFromInternship(long internshipId, long internId) {

        Internship internship = getInternshipById(internshipId);
        TeamMember intern = searchInternInInternship(internship, internId);

        validator.validateInternshipContainsThisIntern(internship, intern);

        removeInternRole(intern);
        internship.getInterns().remove(intern);

        return mapper.toDto(internship);
    }

    @Transactional
    // TODO нужно вернуть дтошку из метода
    public Internship getInternshipById(long id) {

        return internshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + id));
    }

    @Override
    public List<InternshipDto> getInternshipsByStatus(InternshipStatus status, InternshipFilterDto filterDto) {

        List<Internship> filteredInternships = internshipRepository.findByStatus(status);

        return internshipFilterService.applyFilters(filteredInternships.stream(), filterDto)
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<InternshipDto> getInternshipsByRole(TeamRole role, InternshipFilterDto filterDto) {

        List<Internship> filteredInternships = internshipRepository.findAll().stream()
                .filter(internship -> internship.getInterns().stream()
                        .anyMatch(intern -> intern.getRoles().contains(role)))
                .toList();
        // TODO я думаю это лишнее 😇👍🏽
        Stream<Internship> internshipsStream = filteredInternships.stream();

        return internshipFilterService.applyFilters(internshipsStream, filterDto)
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<InternshipDto> getAllInternships(InternshipFilterDto filterDto) {

        Stream<Internship> internshipsStream = internshipRepository.findAll().stream();

        return internshipFilterService.applyFilters(internshipsStream, filterDto)
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public Internship updateInternsRoles(long internshipId, TeamMember teamMember) {

        Internship internship = getInternshipById(internshipId);

        internship.getInterns().stream()
                .filter(intern -> intern.getId().equals(teamMember.getId()))
                .findFirst()
                .ifPresent(intern -> intern.setRoles(teamMember.getRoles()));

        return internship;
    }

    private TeamMember searchInternInInternship(Internship internship, long internId) {

        return internship.getInterns().stream()
                .filter(teamMember -> teamMember.getId().equals(internId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Intern with id: " + internId
                        + "not found in the internship: " + internship.getId()));
    }
    // TODO думаю стоит убрать проверку getRoles().isEmpty() и нужно присвоить роль
    private void removeInternRole(TeamMember intern) {

        if (!intern.getRoles().isEmpty())
            intern.getRoles().remove(TeamRole.INTERN);
    }

    private void changeInternRole(TeamMember teamMember, TeamRole newRole) {

        List<TeamRole> roles = teamMember.getRoles();

        if (roles == null) {roles = new ArrayList<>();}

        roles.remove(INTERN);

        if (!roles.contains(newRole)) {
            roles.add(newRole);
        }
    }
}
