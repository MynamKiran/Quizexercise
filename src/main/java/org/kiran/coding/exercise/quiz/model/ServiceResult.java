package org.kiran.coding.exercise.quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResult {

    public String category;
    public String type;
    public String difficulty;
    public String question;
    public String correct_answer;
    public List<String> incorrect_answers;
}
