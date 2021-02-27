package org.kiran.coding.exercise.quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {

    public String type;
    public String difficulty;
    public String question;
    public List<String> all_answers;
    public String correct_answer;
}
