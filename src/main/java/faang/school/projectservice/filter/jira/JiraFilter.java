package faang.school.projectservice.filter.jira;

import faang.school.projectservice.dto.jira.JiraDto;

public interface JiraFilter {

    boolean isApplicable(JiraDto dto);

    String addFilter(JiraDto dto);
}
