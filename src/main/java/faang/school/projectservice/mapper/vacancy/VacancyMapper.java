package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mappings({
            @Mapping(source = "project.id", target = "projectId"),
            @Mapping(source = "candidates", target = "candidatesIds", qualifiedByName = "mapCandidatesToCandidatesIds"),
            @Mapping(source = "status", target = "status", qualifiedByName = "mapVacancyStatus"),
            @Mapping(source = "workSchedule", target = "workSchedule", qualifiedByName = "mapScheduleStatus")
    })
    VacancyDto toDto(Vacancy vacancy);
    @Mappings({
            @Mapping(source = "projectId", target = "project.id"),
            @Mapping(source = "candidatesIds", target = "candidates", qualifiedByName = "mapCandidatesIdsToCandidates"),
            @Mapping(source = "status", target = "status", qualifiedByName = "mapVacancyStatus"),
            @Mapping(source = "workSchedule", target = "workSchedule", qualifiedByName = "mapScheduleStatus")
    })
    Vacancy toEntity(VacancyDto vacancyDto);

    @Named("mapCandidatesToCandidatesIds")
    default List<Long> mapCandidatesToCandidatesIds(List<Candidate> candidates) {
        return candidates.stream()
                .map(Candidate::getId)
                .collect(Collectors.toList());
    }

    @Named("mapCandidatesIdsToCandidates")
    default List<Candidate> mapCandidatesIdsToCandidates(List<Long> candidates) {
        return candidates.stream()
                .map(id -> {
                    Candidate candidate = new Candidate();
                    candidate.setId(id);
                    return candidate;
                })
                .collect(Collectors.toList());
    }

    @Named("mapVacancyStatus")
    default String mapVacancyStatus(VacancyStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("mapVacancyStatus")
    default VacancyStatus mapVacancyStatus(String status) {
        return status != null ? VacancyStatus.valueOf(status) : null;
    }

    @Named("mapScheduleStatus")
    default String mapScheduleStatus(WorkSchedule status) {
        return status != null ? status.name() : null;
    }

    @Named("mapScheduleStatus")
    default WorkSchedule mapScheduleStatus(String status) {
        return status != null ? WorkSchedule.valueOf(status) : null;
    }
}
