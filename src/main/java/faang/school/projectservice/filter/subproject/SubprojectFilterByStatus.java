package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.project.ProjectStatus;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
public class SubprojectFilterByStatus implements SubprojectFilter{
    @Override
    public boolean isApplicable(SubprojectFilterDto filterDto) {
        return nonNull(filterDto.getStatus());
    }

    @Override
    public Stream<Project> apply(Stream<Project> childernProjectsStream, SubprojectFilterDto filterDto) {
        ProjectStatus filterPattern = filterDto.getStatus();
        return childernProjectsStream.filter(child -> child.getStatus().equals(filterPattern));
    }
}
