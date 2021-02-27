package org.kiran.coding.exercise.quiz.controller;

import org.kiran.coding.exercise.quiz.model.QuizData;
import org.kiran.coding.exercise.quiz.service.QuizProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value ="/coding/exercise/quiz")
public class QuizController {

    private final QuizProcessor quizProcessor;

    public QuizController(QuizProcessor quizProcessor){
        this.quizProcessor = quizProcessor;
    }

    /**
     * Get quiz questions and answers.
     *
     * @return the quiz data
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<QuizData> getMessage(){
    return quizProcessor.processRequest();

    }

}
