package faang.school.projectservice.util;

public class JiraPath {
    public static final String GET_TASK = "/rest/api/3/issue/{issueKey}";
    public static final String CREATE_TASK = "/rest/api/2/issue";
    public static final String UPDATE_TASK = "/rest/api/2/issue/{issueKey}";
    public static final String UPDATE_LINK_TASK = "/rest/api/3/issueLink";
    public static final String GET_TRANSITIONS = "/rest/api/3/issue/{issueKey}/transitions";
    public static final String SEARCH = "/rest/api/2/search";
}
