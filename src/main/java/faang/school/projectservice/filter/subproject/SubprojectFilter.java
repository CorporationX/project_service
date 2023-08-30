package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.model.project.Project;

import java.util.stream.Stream;

public interface SubprojectFilter {
    boolean isApplicable(SubprojectFilterDto filterDto);

    Stream<Project> apply(Stream<Project> childernProjectsStream, SubprojectFilterDto filterDto);
}
