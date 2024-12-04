package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.teamMember.CreateTeamMemberDto;
import faang.school.projectservice.dto.teamMember.ResponseTeamMemberDto;
import faang.school.projectservice.dto.teamMember.UpdateTeamMemberDto;
import faang.school.projectservice.model.team.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TeamMemberMapper {
    @Mapping(source = "stages", target = "stageIds", qualifiedByName = "mapStageToIds")
    CreateTeamMemberDto toCreateDto(TeamMember teamMember);

    @Mapping(target = "stages", ignore = true)
    TeamMember toEntity(CreateTeamMemberDto createTeamMemberDto);

    @Mapping(source = "stages", target = "stageIds", qualifiedByName = "mapStageToIds")
    UpdateTeamMemberDto toUpdateDto(TeamMember teamMember);

    @Mapping(target = "stages", ignore = true)
    TeamMember toEntity(UpdateTeamMemberDto updateTeamMemberDto);

    @Mapping(source = "stages", target = "stageIds", qualifiedByName = "mapStageToIds")
    @Mapping(source = "team.id", target = "teamId")
    ResponseTeamMemberDto toResponseDto(TeamMember teamMember);

    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "team", ignore = true)
    TeamMember toEntity(ResponseTeamMemberDto createTeamMemberDto);

    List<CreateTeamMemberDto> toCreateDto(List<TeamMember> teamMembers);

    List<UpdateTeamMemberDto> toUpdateDto(List<TeamMember> teamMembers);

    List<ResponseTeamMemberDto> toResponseDto(List<TeamMember> teamMembers);

    List<TeamMember> createDtosToEntities(List<CreateTeamMemberDto> createTeamMemberDtos);

    List<TeamMember> updateDtosToEntities(List<UpdateTeamMemberDto> updateTeamMemberDtos);

    List<TeamMember> responseDtosToEntities(List<ResponseTeamMemberDto> responseTeamMemberDtos);

    @Named("mapStageToIds")
    default List<Long> mapStageIds(List<Stage> stages) {
        return stages.stream()
                .map(Stage::getStageId)
                .toList();
    }
}
