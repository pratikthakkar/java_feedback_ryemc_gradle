package com.example.feedbackapp.service;

import com.example.feedbackapp.model.Feedback;
import com.example.feedbackapp.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public Feedback saveFeedback(Feedback feedback) {
        feedback.setSubmittedAt(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    // Analytics: count by category
    public Map<String, Long> getFeedbackCountByCategory() {
        return feedbackRepository.findAll().stream()
                .collect(Collectors.groupingBy(Feedback::getCategory, Collectors.counting()));
    }

    // Analytics: average rating
    public double getAverageRating() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        if (feedbacks.isEmpty()) return 0.0;
        return feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0.0);
    }

    // Analytics: feedback count by day
    public Map<LocalDate, Long> getFeedbackCountByDay() {
        return feedbackRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        f -> f.getSubmittedAt().toLocalDate(),
                        Collectors.counting()
                ));
    }
}
