package faang.school.projectservice.dto.client.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipDto {
    private Long id;
    private Long projectId;// Project;
    private Long mentorId;//TeamMember член команды Ссылка на наставника
    private TeamRole role;//Роль стажера в рамках стажировки
    private List<Long> internsId;//Список стажеров, участвующих в данной стажировке
    private LocalDateTime startDate;//Дата и время начала стажировки
    private LocalDateTime endDate;//Дата и время окончания стажировки
    private InternshipStatus status;//Статус стажировки
    private String description;//Описание стажировки
    private String name;//Название стажировки



}
