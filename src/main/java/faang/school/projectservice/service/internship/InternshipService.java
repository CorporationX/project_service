package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.internship.InternshipFilterDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;

    public InternshipDto create(@Valid @NotNull InternshipDto internshipDto) {
        validateDurationOfInternship(internshipDto);

        if (internshipDto.getProjectId() == null) {
            throw new DataValidationException("Стажировка должна относиться к проекту");
        }

        Internship internshipEntity = internshipMapper.toEntity(internshipDto);

        List<TeamMember> teamMembers = internshipDto.getInternsIds()
                .stream()
                .map(teamMemberRepository::findById).collect(Collectors.toList());
        internshipEntity.setInterns(teamMembers);

        if (teamMembers.isEmpty()) {
            throw new DataValidationException("Список членов команды пустой");
        }

        boolean mentorInTeam = teamMembers.stream().
                anyMatch(teamMember -> teamMember.getUserId().equals(internshipDto.getMentorId()));
        if (!mentorInTeam) {
            throw new DataValidationException("Указанный ментор не является членом команды проекта");
        }

        internshipEntity = internshipRepository.save(internshipEntity);
        return internshipMapper.toDto(internshipEntity);
    }

    public void updateInternship(@Valid @NotNull InternshipDto internshipDto) {

        Internship internshipEntity = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Стажировка не найдена"));

        if (internshipEntity.getStartDate().isBefore(LocalDateTime.now()) &&
                internshipEntity.getStatus().equals(InternshipStatus.IN_PROGRESS)) {
            throw new DataValidationException("стажировка началась, добавление новых стажеров невозможно");
        }

        internshipEntity.setStartDate(internshipDto.getStartDate());
        internshipEntity.setEndDate(internshipDto.getEndDate());

        List<TeamMember> teamMembers = internshipDto.getInternsIds()
                .stream().map(teamMemberRepository::findById).collect(Collectors.toList());
        internshipEntity.setInterns(teamMembers);

        validateDurationOfInternship(internshipDto);

        for (TeamMember teamMember : teamMembers) {
            boolean completeInternship = teamMember.getStages().stream()
                                .allMatch(stage -> stage.getTasks().stream()
                                .allMatch(task -> task.getStatus().equals(TaskStatus.DONE)));

        if (completeInternship) {
            teamMember.getRoles().remove(TeamRole.INTERN);
            teamMember.getRoles().add(TeamRole.DEVELOPER);

        } else {
            internshipEntity.getInterns().remove(teamMember);
        }}

        if (internshipEntity.getEndDate().isBefore(LocalDateTime.now()) &&
                internshipEntity.getStatus().equals(InternshipStatus.IN_PROGRESS)) {
            internshipEntity.setStatus(InternshipStatus.COMPLETED);
            internshipEntity.setEndDate(LocalDateTime.now());
        }

        internshipRepository.save(internshipEntity);
    }

    public List<InternshipDto> getAllInternshipByStatusAndRole(Long projectId, InternshipFilterDto filters) {
        List<Internship> allInternship = internshipRepository.findAll();
        return allInternship.stream()
                .filter(internship -> internship.getProject().getId().equals(projectId))
                .filter(internship -> internship.getStatus() == filters.getStatus())
                .filter(internship -> internship.getInterns()
                        .stream().anyMatch(intern -> intern.getRoles()
                                .contains(filters.getIntern())))
                .map(internshipMapper::toDto).toList();
    }

    public List<InternshipDto> getAllInternship() {
        List<Internship> internships = internshipRepository.findAll();
        return internshipMapper.toListDto(internships);
    }

    public InternshipDto getInternshipById(Long id) {
        Internship internships = internshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Интернатура с ID " + id + " не найдена"));
        return internshipMapper.toDto(internships);
    }

    private void validateDurationOfInternship(@NotNull InternshipDto internshipDto) {
        Period period = Period.between(internshipDto.getStartDate().toLocalDate(), internshipDto.getEndDate().toLocalDate());
        if (period.getMonths() > 3) {
            throw new DataValidationException("Стажировка не может длится больше трех месяцев");
        }
    }
}
