package faang.school.projectservice.mapper.jira;

import faang.school.projectservice.dto.jira.IssueStatusTransition;
import faang.school.projectservice.dto.jira.IssueStatusUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface IssueStatusMapper {

    @Mapping(target = "transition", source = "status", qualifiedByName = "toTransition")
    IssueStatusTransition toTransition(IssueStatusUpdateDto issueStatusUpdateDto);

    @Named("toTransition")
    default IssueStatusTransition.Transition toTransition(String toStatus) {
        if (toStatus.equalsIgnoreCase("to do")) {
            return new IssueStatusTransition.Transition("11");
        } else if (toStatus.equalsIgnoreCase("in progress")) {
            return new IssueStatusTransition.Transition("21");
        } else if (toStatus.equalsIgnoreCase("done")) {
            return new IssueStatusTransition.Transition("31");
        }
        return null;
    }
}
