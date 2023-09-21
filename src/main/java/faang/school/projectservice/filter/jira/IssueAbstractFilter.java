package faang.school.projectservice.filter.jira;


public abstract class IssueAbstractFilter implements IssueFilter {

    public StringBuilder concatFilter(StringBuilder filter, String field, String value) {
        if (filter.isEmpty()) {
            return filter.append(field).append("=").append("\"").append(value).append("\"");
        }
        return filter.append("&").append(field).append("=").append("\"").append(value).append("\"");
    }
}
