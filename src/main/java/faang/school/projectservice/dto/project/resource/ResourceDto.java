package faang.school.projectservice.dto.project.resource;

import faang.school.projectservice.model.ResourceType;

import java.math.BigInteger;

public record ResourceDto(Long id, String name, String key, BigInteger size, ResourceType type) { }
