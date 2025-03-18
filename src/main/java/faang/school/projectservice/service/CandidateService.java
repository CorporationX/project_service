package faang.school.projectservice.service;

import faang.school.projectservice.model.Candidate;

import java.util.List;

public interface CandidateService {
    List<Candidate> getAllCandidatesAttachedToProjectVacancy(long vacancyId, long projectId);
}
