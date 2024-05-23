package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "candidatesIds", source = "candidates", qualifiedByName = "getIdFromCandidate")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(source = "curator.userId", target = "curatorId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stages", target = "stageIds", qualifiedByName = "getIdFromStage")
    InitiativeDto toDto(Initiative initiative);

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "candidates", source = "candidatesIds", qualifiedByName = "getCandidateFromId")
    Vacancy toEntity(VacancyDto vacancyDto);

    @Named("getIdFromCandidate")
    default Long getIdFromCandidate(Candidate candidate) {
        return candidate.getId();
    }

    @Named("getCandidateFromId")
    default Candidate getCandidateFromId(Long id) {
        return Candidate.builder().id(id).build();
    }


    @Mapping(source = "curatorId", target = "curator.userId")
    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "stageIds", target = "stages", qualifiedByName = "getStageFromId")
    Initiative toEntity(InitiativeDto initiativeDto);

    @Named("getIdFromStage")
    default Long getIdFromStage(Stage stage) {
        return stage.getStageId();
    }

    @Named("getStageFromId")
    default Stage getStageFromId(Long id) {
        return Stage.builder().stageId(id).build();
    }
}