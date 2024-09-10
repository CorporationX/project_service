package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.dto.client.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.client.subproject.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {

    @Mapping(target = "status",ignore = true)
    CreateSubProjectDto mapToSubDto(ProjectDto projectDto);

    Project mapToEntity(CreateSubProjectDto createSubProjectDto);

    ProjectDto mapToProjectDto(Project project);
}


//
//
//@Mapping(source = "receiver.id", target = "receiverId")
//@Mapping(source = "requester.id", target = "requesterId")
//@Mapping(source = "skills", target = "skillsIds", qualifiedByName = "mapSkillsReqsToSkillsReqsIds")
//RecommendationRequestDto mapToDto(RecommendationRequest recommendationRequest);
//
//List<RecommendationRequestDto> mapToDto(List<RecommendationRequest> recommendationRequests);
//
//@Mapping(source = "requesterId", target = "requester.id")
//@Mapping(source = "receiverId", target = "receiver.id")
//@Mapping(target = "skills", ignore = true)
//RecommendationRequest mapToEntity(RecommendationRequestDto recommendationRequestDto);
//
//RejectionDto mapToRejectionDto(RecommendationRequest recommendationRequest);
//
//@Named("mapSkillsReqsToSkillsReqsIds")
//static List<Long> mapSkillsReqsToSkillsReqsIds(List<SkillRequest> skillRequests) {
//    return skillRequests.stream()
//            .map(SkillRequest::getId)
//            .toList();
//}
//}