package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.mappers.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        if (!hasValidMentor(internshipDto)) {
            log.error("Не найден валидный ментор для стажировки");
            throw new IllegalArgumentException("Не найден валидный ментор для стажировки");
        }
        if (!isDurationValid(internshipDto)) {
            log.error("Длительность стажировки > 3 мес.");
            throw new IllegalArgumentException("Длительность стажировки не может превышать 3 месяцев");
        }

        Internship internship = internshipMapper.toEntity(internshipDto);
        log.info("Создаю новую сущность.");
        internship = internshipRepository.save(internship);
        log.info("Сохраняю новую сущность в БД.");
        return internshipMapper.toDto(internship);
    }

    public InternshipDto updateInternship(long id, InternshipDto internshipDto) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Стажировка с ID: {} не найдена", id);
                    return new IllegalArgumentException("Стажировка не найдена с ID: " + id);
                });

        if (InternshipStatus.COMPLETED.equals(internshipDto.getStatus())) {
            log.info("Стажировка окончена, меняю роль стажеру.");
            updateTraineeRoles(internship);
        }

        internship.setStatus(InternshipStatus.valueOf(internshipDto.getStatus()));
        log.info("Меняю статус.");
        internship.setEndDate(internshipDto.getEndDate());
        log.info("Меняю дату окончания стажировки");

        internship = internshipRepository.save(internship);
        log.info("Сохраняю изменения в БД.");
        return internshipMapper.toDto(internship);
    }

    public List<InternshipDto> getAllInternships(String status, String description) {
        List<Internship> internships = internshipRepository.findAll();

        return internships.stream()
                .filter(i -> status == null || status.equals(i.getStatus().name()))
                .filter(i -> description == null || i.getDescription().contains(description))
                .map(internshipMapper::toDto)
                .collect(Collectors.toList());
    }

    public InternshipDto getInternshipById(long id) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Стажировка с ID: {} не найдена", id);
                    return new IllegalArgumentException("Стажировка не найдена с ID: " + id);
                });
        return internshipMapper.toDto(internship);
    }

    private boolean hasValidMentor(InternshipDto internshipDto) {
        return internshipDto.getMentorId() != null;
    }

    private boolean isDurationValid(InternshipDto internshipDto) {
        LocalDateTime startDate = internshipDto.getStartDate();
        LocalDateTime endDate = internshipDto.getEndDate();
        return startDate != null && endDate != null && startDate.plusMonths(3).isAfter(endDate);
    }

    private void updateTraineeRoles(Internship internship) {
        if (allTasksCompleted(internship)) {
            internship.getInterns().forEach(intern -> assignNewRole(intern.getId(), internship));
        } else {
            internship.getInterns().forEach(intern -> removeFromProject(intern.getId(), internship));
        }
    }

    private boolean allTasksCompleted(Internship internship) {
        return true; // Пример для демонстрации
    }

    private void assignNewRole(Long internId, Internship internship) {
        TeamMember intern = teamMemberRepository.findById(internId);

        String internshipName = internship.getName();

        TeamRole internNewRole = TeamRole.getAll().stream()
                .filter(role -> role.name().equalsIgnoreCase(internshipName))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Такой стажировки не существует");
                    return new IllegalArgumentException("Такой стажировки не существует");
                });

        intern.getRoles().add(internNewRole);

        log.info("Добавлена роль {} стажеру с ID: {}", internshipName, internId);

        teamMemberRepository.save(intern);
    }


    private void removeFromProject(Long internId, Internship internship) {
        TeamMember intern = teamMemberRepository.findById(internId);

        internshipRepository.removeInternFromProject(intern.getId(), internship.getId());

        log.info("Стажер с ID: {} был удален из проекта", internId);
    }
}
