package faang.school.projectservice.filter.jira;


public abstract class IssueAbstractFilter implements IssueFilter {

    public StringBuilder concatFilter(StringBuilder filter, String field, String value) {
        if (filter.charAt(filter.length() - 1) == '=') {
            return filter.append(field).append("=").append(value);
        }
        return filter.append("&").append(field).append("=").append(value);
    }
}
