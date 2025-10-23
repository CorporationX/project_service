package faang.school.projectservice.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(example = """
        {
             "name": "updatedFirstProjectFirstVacancy",
             "description":"Test vacancy updating service",
             "projectId":2,
             "status":"OPEN",
             "position":"DEVELOPER",
             "count":5,
             "workSchedule":"FULL_TIME",
             "candidates":[
                 {
                     "id":"1",
                     "userId":"2",
                     "username":"testCandidate",
                     "candidateStatus":"WAITING_RESPONSE",
                     "vacancyId":7
                 }
             ]
         }
        """)
public record UpdateVacancyDto(
        String name,
        String description,
        Long projectId,
        @JsonProperty("status")
        VacancyStatus vacancyStatus,
        WorkSchedule workSchedule,
        int count,
        TeamRole position,
        List<CandidateDto> candidates
) {
}
