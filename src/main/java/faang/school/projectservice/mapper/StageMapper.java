package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDtoForRequest;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "tasks", target = "tasksIds", qualifiedByName = "mapTask")
    @Mapping(source = "executors", target = "executorIds", qualifiedByName = "mapExecutor")
    StageDtoForRequest toDto(Stage stage);

    List<StageDtoForRequest> toDto(List<Stage> stages);

    Stream<StageDtoForRequest> toDto(Stream<Stage> stages);

    @Mapping(source = "project", target = "project.id")
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "executors", ignore = true)
    Stage toEntity(StageDtoForRequest stageDto);

    default List<Long> mapTask(List<Task> tasks) {
        return tasks.stream().map(Task::getId).toList();
    }

    default List<Long> mapExecutor(List<TeamMember> teamMembers) {
        return teamMembers.stream().map(TeamMember::getId).toList();
    }

//    private Long stageId;
//    private String stageName;
//    private Project project;
//    private List<StageRoles> stageRoles;
//    private List<Task> tasks;
//    private List<TeamMember> executors;

//    private Long stageId;
//    private String stageName;
//    private Long projectId;
//    private List<StageRoles> stageRoles;
//    private List<Long> taskIds;
//    private List<Long> executorIds;
//
//    List<MentorshipRequestDto> toDto(List<MentorshipRequest> requests);
//
//    Stream<MentorshipRequestDto> toDto(Stream<MentorshipRequest> requests);
//
//    @Mapping(source = "requesterId", target = "requester.id")
//    @Mapping(source = "receiverId", target = "receiver.id")
//    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);
}
