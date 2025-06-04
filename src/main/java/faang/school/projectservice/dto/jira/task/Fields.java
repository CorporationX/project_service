package faang.school.projectservice.dto.jira.task;

public class Fields{
    public Assignee assignee;
    public Issuetype issuetype;
    public Parent parent;
    public Project project;
    public String summary;
}

class Assignee{
    public String id;
}

class Issuetype{
    public String id;
}

class Parent{
    public String key;
}

class Project{
    public String id;
}
