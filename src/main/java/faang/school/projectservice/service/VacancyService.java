package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exceptions.EntityNotFoundException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final TeamMemberRepository teamMemberRepository;
    public Map<String, String> create(VacancyDto vacancyDto) throws ValidationException {
        TeamMember memberInCharge = teamMemberRepository.findById(vacancyDto.getCreatedBy());
        if(memberInCharge == null) {
            throw new EntityNotFoundException("Team member not found");
        }
        boolean isHr = memberInCharge.getRoles().contains(TeamRole.OWNER) || memberInCharge.getRoles().contains(TeamRole.MANAGER);
        if(!isHr) {
            throw new ValidationException("User must be either manager or owner");
        }
        Vacancy createdVacancy = vacancyRepository
                .create(vacancyDto.getName(), vacancyDto.getDescription(), vacancyDto.getProjectId(),
                        vacancyDto.getCreatedBy(), vacancyDto.getUpdatedBy(), vacancyDto.getStatus().toString(),
                        vacancyDto.getSalary(), vacancyDto.getWorkSchedule().toString(), vacancyDto.getCount());

        return
                Map.of("message", "vacancy created",
                        "status", HttpStatus.CREATED.toString());
    }
}
