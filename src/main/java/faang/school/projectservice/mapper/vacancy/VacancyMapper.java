package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.model.vacancy.Vacancy;
import faang.school.projectservice.model.vacancy.VacancyStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mappings({
            @Mapping(target = "projectId", source = "project.id"),
            @Mapping(target = "candidateIds", source = "candidates", qualifiedByName = "candidateToId"),
            @Mapping(target = "status", source = "status", qualifiedByName = "statusToString"),
            @Mapping(target = "workSchedule", source = "workSchedule", qualifiedByName = "scheduleToString")
    })
    VacancyDto toDto(Vacancy vacancy);

    @Mappings({
            @Mapping(target = "project.id", source = "projectId"),
            @Mapping(target = "candidates", source = "candidateIds", qualifiedByName = "idToCandidate"),
            @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus"),
            @Mapping(target = "workSchedule", source = "workSchedule", qualifiedByName = "stringToSchedule")
    })
    Vacancy toEntity(VacancyDto vacancyDto);

    @Named("candidateToId")
    default List<Long> candidateToId(List<Candidate> candidates) {
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }

    @Named("idToCandidate")
    default List<Candidate> idToCandidate(List<Long> candidateIds) {
        return candidateIds.stream()
                .map(id -> {
                    Candidate candidate = new Candidate();
                    candidate.setId(id);
                    return candidate;
                })
                .toList();
    }

    @Named("statusToString")
    default String statusToString(VacancyStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToStatus")
    default VacancyStatus stringToStatus(String status) {
        return status != null ? VacancyStatus.valueOf(status) : null;
    }

    @Named("scheduleToString")
    default String scheduleToString(WorkSchedule schedule) {
        return schedule != null ? schedule.name() : null;
    }

    @Named("stringToSchedule")
    default WorkSchedule stringToSchedule(String schedule) {
        return schedule != null ? WorkSchedule.valueOf(schedule) : null;
    }
}