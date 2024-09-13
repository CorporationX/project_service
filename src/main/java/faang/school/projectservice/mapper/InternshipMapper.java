package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.TeamRoleDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamRole;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {
    InternshipDto internshipToInternshipDto(Internship internship);

    Internship createInternshipDtoToInternship(CreateInternshipDto internshipDto);

    Internship internshipDtoToInternship(InternshipDto internshipDto);

    List<Internship> internshipDtosToInternships(List<InternshipDto> internshipDtos);

    List<InternshipDto> internshipsToInternshipDtos(List<Internship> internships);

    TeamRole teamRoleDtoToTeamRole(TeamRoleDto teamRoleDto);
}
