package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.model.project.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
public class SubprojectFilterByName implements SubprojectFilter{
    @Override
    public boolean isApplicable(SubprojectFilterDto filterDto) {
        return nonNull(filterDto.getName());
    }

    @Override
    public Stream<Project> apply(Stream<Project> childernProjectsStream, SubprojectFilterDto filterDto) {
        String filterPattern = filterDto.getName();
        return childernProjectsStream.filter(child -> child.getName().contains(filterPattern));
    }
}
