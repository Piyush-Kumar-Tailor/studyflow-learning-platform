package com.studyflow.backend.exam.service;

import com.studyflow.backend.exam.dto.ExamRequest;
import com.studyflow.backend.exam.dto.ExamResponse;
import com.studyflow.backend.exam.dto.ExamStatusRequest;
import com.studyflow.backend.exam.entity.Exam;
import com.studyflow.backend.exam.repository.ExamRepository;
import com.studyflow.backend.user.enums.ExamCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    // Get active exams with optional search and category filters
    public Page<ExamResponse> getExams(
            String search,
            ExamCategory category,
            int page,
            int size
    ) {
        validatePagination(page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "name")
        );

        String searchTerm = search == null ? "" : search.trim();

        Page<Exam> exams;

        if (category != null && !searchTerm.isEmpty()) {
            exams = examRepository
                    .findByCategoryAndNameContainingIgnoreCaseAndActiveTrue(
                            category,
                            searchTerm,
                            pageable
                    );
        } else if (category != null) {
            exams = examRepository.findByCategoryAndActiveTrue(
                    category,
                    pageable
            );
        } else if (!searchTerm.isEmpty()) {
            exams = examRepository
                    .findByNameContainingIgnoreCaseAndActiveTrue(
                            searchTerm,
                            pageable
                    );
        } else {
            exams = examRepository.findByActiveTrue(pageable);
        }

        return exams.map(ExamResponse::from);
    }

    // Get a single active exam
    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Active examination not found with ID: " + id
                ));

        return ExamResponse.from(exam);
    }

    // Admin: get any exam, including inactive exams
    public ExamResponse getExamByIdForAdmin(Long id) {
        return ExamResponse.from(findExamOrThrow(id));
    }

    // Admin: create an exam
    @Transactional
    public ExamResponse createExam(ExamRequest request) {

        String normalizedCode = normalizeCode(request.code());

        if (examRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An examination with this code already exists"
            );
        }

        Exam exam = new Exam();

        applyRequest(exam, request, normalizedCode);

        exam.setActive(true);

        Exam savedExam = examRepository.save(exam);

        return ExamResponse.from(savedExam);
    }

    // Admin: update an existing exam
    @Transactional
    public ExamResponse updateExam(Long id, ExamRequest request) {

        Exam exam = findExamOrThrow(id);

        String normalizedCode = normalizeCode(request.code());

        if (examRepository.existsByCodeIgnoreCaseAndIdNot(
                normalizedCode,
                id
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another examination already uses this code"
            );
        }

        applyRequest(exam, request, normalizedCode);

        Exam updatedExam = examRepository.save(exam);

        return ExamResponse.from(updatedExam);
    }

    // Admin: activate or deactivate an exam
    @Transactional
    public ExamResponse updateExamStatus(
            Long id,
            ExamStatusRequest request
    ) {
        Exam exam = findExamOrThrow(id);

        exam.setActive(request.active());

        Exam updatedExam = examRepository.save(exam);

        return ExamResponse.from(updatedExam);
    }

    private void applyRequest(
            Exam exam,
            ExamRequest request,
            String normalizedCode
    ) {
        exam.setName(request.name().trim());
        exam.setCode(normalizedCode);
        exam.setCategory(request.category());

        exam.setConductingBody(
                normalizeOptionalText(request.conductingBody())
        );

        exam.setDescription(
                normalizeOptionalText(request.description())
        );

        exam.setOfficialWebsite(
                normalizeOptionalText(request.officialWebsite())
        );
    }

    private Exam findExamOrThrow(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Examination not found with ID: " + id
                ));
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private void validatePagination(int page, int size) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and 100"
            );
        }
    }
}