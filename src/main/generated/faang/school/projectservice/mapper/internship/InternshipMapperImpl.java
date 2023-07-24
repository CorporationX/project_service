package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.ResponseInternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TeamMember;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-24T13:27:04+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.7 (Oracle Corporation)"
)
@Component
public class InternshipMapperImpl implements InternshipMapper {

    @Override
    public Internship createDtoToEntity(CreateInternshipDto dto) {
        if ( dto == null ) {
            return null;
        }

        Internship.InternshipBuilder internship = Internship.builder();

        internship.startDate( dto.getStartDate() );
        internship.endDate( dto.getEndDate() );
        internship.name( dto.getName() );
        internship.createdBy( dto.getCreatedBy() );

        return internship.build();
    }

    @Override
    public ResponseInternshipDto entityToResponseDto(Internship internship) {
        if ( internship == null ) {
            return null;
        }

        ResponseInternshipDto.ResponseInternshipDtoBuilder responseInternshipDto = ResponseInternshipDto.builder();

        responseInternshipDto.projectId( internshipProjectId( internship ) );
        responseInternshipDto.mentorId( internshipMentorId( internship ) );
        responseInternshipDto.internIds( internsToInternIds( internship.getInterns() ) );
        responseInternshipDto.scheduleId( internshipScheduleId( internship ) );
        responseInternshipDto.id( internship.getId() );
        responseInternshipDto.startDate( internship.getStartDate() );
        responseInternshipDto.endDate( internship.getEndDate() );
        responseInternshipDto.status( internship.getStatus() );
        responseInternshipDto.description( internship.getDescription() );
        responseInternshipDto.name( internship.getName() );
        responseInternshipDto.createdAt( internship.getCreatedAt() );
        responseInternshipDto.updatedAt( internship.getUpdatedAt() );
        responseInternshipDto.createdBy( internship.getCreatedBy() );
        responseInternshipDto.updatedBy( internship.getUpdatedBy() );

        return responseInternshipDto.build();
    }

    @Override
    public List<ResponseInternshipDto> entityListToDtoList(List<Internship> internships) {
        if ( internships == null ) {
            return null;
        }

        List<ResponseInternshipDto> list = new ArrayList<ResponseInternshipDto>( internships.size() );
        for ( Internship internship : internships ) {
            list.add( entityToResponseDto( internship ) );
        }

        return list;
    }

    private Long internshipProjectId(Internship internship) {
        if ( internship == null ) {
            return null;
        }
        Project project = internship.getProject();
        if ( project == null ) {
            return null;
        }
        Long id = project.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long internshipMentorId(Internship internship) {
        if ( internship == null ) {
            return null;
        }
        TeamMember mentor = internship.getMentor();
        if ( mentor == null ) {
            return null;
        }
        Long id = mentor.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long internshipScheduleId(Internship internship) {
        if ( internship == null ) {
            return null;
        }
        Schedule schedule = internship.getSchedule();
        if ( schedule == null ) {
            return null;
        }
        Long id = schedule.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
