package faang.school.projectservice.cache;

import faang.school.projectservice.jpa.JiraProjectRepository;
import faang.school.projectservice.model.jira.JiraProject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JiraProjectCache {
    private final Map<String, JiraProject> jiraProjects;
    private final JiraProjectRepository jiraProjectRepository;

    public JiraProjectCache(JiraProjectRepository jiraProjectRepository) {
        this.jiraProjectRepository = jiraProjectRepository;
        this.jiraProjects = new HashMap<>();
        fillCache();
    }

    public Optional<JiraProject> get(String key) {
        return Optional.of(jiraProjects.get(key));
    }

    private void fillCache() {
        jiraProjectRepository.findAll().forEach(jiraProject -> jiraProjects.put(jiraProject.getKey(), jiraProject));
    }
}
