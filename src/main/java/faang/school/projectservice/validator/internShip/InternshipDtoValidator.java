package faang.school.projectservice.validator.internShip;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.internShip.InternshipDtoValidateException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@NoArgsConstructor
public class InternshipDtoValidator {

    private final String MSG_INVALID_PROJECT = "The internship does not have a project.";
    private final String MSG_INVALID_INTERNS = "У стажировки отсутствуют стажёры.";
    private final int DURATION_INTERNSHIP_IN_MONTH = 3;
    private final String MSG_INVALID_ENDDATE = "Срок стажировки меньше %d месяце".formatted(DURATION_INTERNSHIP_IN_MONTH);
    private final String MSG_INVALID_MENTOR = "У стажировки отсутствует наставник.";

    //region Валидация dto в контроллере при создании и обновлении на пустые поля
    public boolean validateInternshipDto(InternshipDto dto) {
        validateExistenceProject(dto.getProjectId());
        validateExistenceInterns(dto.getInternIds());
        validateDates(dto.getStartDate(), dto.getEndDate());
        validateExistenceMentors(dto.getMentorId());
        return true;
    }

    private void validateExistenceMentors(Long mentorId) {
        if (mentorId == null) {
            throw new InternshipDtoValidateException(MSG_INVALID_MENTOR);
        }
    }

    private void validateExistenceInterns(List<Long> internsId) {
        if (internsId == null || internsId.isEmpty()) {
            throw new InternshipDtoValidateException(MSG_INVALID_INTERNS);
        }
    }

    private void validateExistenceProject(Long projectId) {
        if (projectId == null) {
            throw new InternshipDtoValidateException(MSG_INVALID_PROJECT);
        }
    }

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            throw new InternshipDtoValidateException("Отсутствует дата начала стажировки");
        }
        if (endDate == null) {
            throw new InternshipDtoValidateException("Отсутствует дата окончании стажировки");
        }
        if (endDate.isBefore(startDate)) {
            throw new InternshipDtoValidateException("Дата окончания стажировки не может быть раньше даты начала.");
        }
        if (endDate.isBefore(startDate.plusMonths(DURATION_INTERNSHIP_IN_MONTH))) {
            throw new InternshipDtoValidateException(MSG_INVALID_ENDDATE);
        }
        if (endDate.isBefore(LocalDateTime.now())) {
            throw new InternshipDtoValidateException("Дата окончания стажировки не может быть в прошлом. Завершите стажировку.");
        }
    }

    //endregion

    // Валидация ментора, который должен состоять в команде проекта.
    public boolean validateMentorIsMember(TeamMember mentor, Project project) {
        boolean resultValidate = project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().contains(mentor));

        if (!resultValidate) {
            throw new InternshipDtoValidateException("Ментор не из команды проекта.");
        }
        return true;
    }

    //region Проверка изменений для update()
    public boolean checkChanges(Internship entity, InternshipDto internShipDto) {
        checkChangeInterns(entity, internShipDto);
        checkChangedStartDate(entity, internShipDto);
        checkChangedEndDate(entity, internShipDto);
        checkChangeProject(entity, internShipDto);
        return true;
    }

    /*
     Стажёры могут досрочно завершать стажировку, значит, у них меняются роли, которых могут быть больше одной.
     Допустим, что одновременно можно проходить только одну стажировку, тогда стажёра без роли INTERN можно удалить из списка.
     Но удалять стажёров, которые прошли стажировку досрочно, и обновлять такую стажировку в БД, я не стал, чтобы
     сохранялась информация о том, что кто-то когда-то проходил какую-то стажировку.

     В методе я сравниваю списки стажёров со списком id стажёров, проверяя состава стажёров на предмет изменения,
     что запрещено во время стажировки.
      */
    private void checkChangeInterns(Internship entity, InternshipDto internShipDto) {
        List<Long> entityInternIds = entity.getInterns().stream().map(TeamMember::getId).toList();
        List<Long> dtoInternIds = internShipDto.getInternIds();

        // если списки стажёров не совпадают
        if (!entityInternIds.equals(dtoInternIds)) {
            throw new InternshipDtoValidateException("Во время стажировки, менять список стажёров нельзя.");
        }
    }

    // Менялась ли дата начала стажировки.
    // У стажировки только два статуса в работе/завершена от чего нельзя создать стажировку на будущее и сдвигать
    // начало стажировки.
    private void checkChangedStartDate(Internship entity, InternshipDto internShipDto) {
        if (!entity.getStartDate().equals(internShipDto.getStartDate())) {
            throw new InternshipDtoValidateException("Дату начала стажировки менять нельзя.");
        }
    }

    // Если менялась дата окончания, то проверка на длительность стажировки.
    private void checkChangedEndDate(Internship entity, InternshipDto internShipDto) {
        LocalDateTime entityEndDate = entity.getEndDate();
        LocalDateTime dtoEndDate = internShipDto.getEndDate();
        if (!entityEndDate.isEqual(dtoEndDate)) {
            LocalDateTime entityStartDate = entity.getStartDate();
            validateDates(entityStartDate, dtoEndDate);
        }
    }

    private void checkChangeProject(Internship internship, InternshipDto internShipDto) {
        Project entityProject = internship.getProject();
        Long dtoProjectId = internShipDto.getProjectId();
        if(!Objects.equals(entityProject.getId(), dtoProjectId)){
            throw new InternshipDtoValidateException("Проект менять нельзя.");
        }
    }
    //endregion

    public void validateInternshipId(Long internshipId) {
        if(internshipId == null){
            throw new InternshipDtoValidateException("the id shouldn't be null");
        }
    }

    public void checkCompletedStatus(InternshipDto dto, Internship entity) {
        InternshipStatus statusDto = dto.getStatus();
        InternshipStatus statusEntity = entity.getStatus();
        InternshipStatus statusCompleted = InternshipStatus.COMPLETED;
        if(statusDto.equals(statusCompleted) && statusEntity.equals(statusCompleted)){
           throw new InternshipDtoValidateException("В завершённой стажировке, нельзя ничего менять.");
        }
    }
}
