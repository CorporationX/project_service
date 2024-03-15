package faang.school.projectservice.config;

import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.service.project.filter.ProjectNameFilter;
import faang.school.projectservice.service.project.filter.ProjectStatusFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public List<ProjectFilter> projectFilters() {
        List<ProjectFilter> filters = new ArrayList<>();
        filters.add(new ProjectNameFilter());
        filters.add(new ProjectStatusFilter());
        return filters;
    }
}
