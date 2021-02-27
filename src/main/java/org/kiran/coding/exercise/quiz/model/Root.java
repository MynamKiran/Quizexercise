package org.kiran.coding.exercise.quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Root {

    public int response_code;
    public List<ServiceResult> results;
}
