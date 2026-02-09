package com.mealmate.repository;

import com.mealmate.model.Feedback;
import com.mealmate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByProvider(User provider);

    List<Feedback> findByConsumer(User consumer);
}
