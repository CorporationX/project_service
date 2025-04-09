package faang.school.projectservice.dto;

import faang.school.projectservice.model.ResourceStatus;

import java.math.BigInteger;

public record ResourceDto(Long id, String name, String key, BigInteger size, ResourceStatus status, Long projectId) {
}
