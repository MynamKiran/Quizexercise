package org.kiran.coding.exercise.quiz.service;

import org.kiran.coding.exercise.quiz.exception.QuizRequestAPIException;
import org.kiran.coding.exercise.quiz.model.Quiz;
import org.kiran.coding.exercise.quiz.model.QuizData;
import org.kiran.coding.exercise.quiz.model.QuizResult;
import org.kiran.coding.exercise.quiz.model.ServiceResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class QuizProcessor {

    private final RestTemplateService restTemplateService;

    @Value("${downstream.service.url1}")
    private String apiUrl1;

    @Value("${downstream.service.url2}")
    private String apiUrl2;

    public QuizProcessor(RestTemplateService restTemplateService){

        this.restTemplateService = restTemplateService;
    }

    /**
     * Process Quiz request.
     *
     * @return the Quiz Data
     */
    public ResponseEntity<QuizData> processRequest() {
        ResponseEntity<QuizData> responseEntity ;
        try {
            responseEntity = ResponseEntity.ok(processQuizData());
        }catch (QuizRequestAPIException ex){
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
       return responseEntity;

    }

    /**
     * Async call.
     * Calling rest template
     * @param url - service URL
     * @return return the service result
     */
    @Async(value = "asyncExecutor")
    public CompletableFuture<List<ServiceResult>> getRestTemplateService(String url){
        return CompletableFuture.completedFuture(restTemplateService.invokeService(url));
    }

    /**
     * Calling the service and processing the data
     * @return return the quiz data
     */
    private QuizData processQuizData(){

        CompletableFuture<List<ServiceResult>> serviceResponseOne = getRestTemplateService(apiUrl1);
        CompletableFuture<List<ServiceResult>> serviceResponseTwo = getRestTemplateService(apiUrl2);
        List<ServiceResult> serviceResults = new ArrayList<>();
        QuizData quizData = new QuizData();

        try {
            if (serviceResponseOne != null) {
              serviceResults = serviceResponseOne.get();
            }
            if(serviceResponseTwo!=null)
            {
               serviceResults.addAll(serviceResponseTwo.get());
            }
        } catch (InterruptedException | ExecutionException e) {
                throw new QuizRequestAPIException("Async exception occurred " +e.getMessage());
        }
        if(!CollectionUtils.isEmpty(serviceResults)){
            quizData.setQuiz(buildQuizObject(serviceResults));
        }

        return quizData;
    }

    /**
     * Processing the service result object
     * @param serviceResultList - service Result object
     * @return return the list of quiz objects
     */
    private List<Quiz> buildQuizObject(List<ServiceResult> serviceResultList){
        List<Quiz> quiz = new ArrayList<>();
        Map<String, List<ServiceResult>> resultMap= serviceResultList.stream().collect(Collectors.groupingBy(ServiceResult::getCategory));
        resultMap.entrySet().forEach(e -> quiz.add( populateQuizObject(e)));
        return quiz;
    }

    /**
     * populating quiz object
     * @param e is map entry
     * @return return the quiz object
     */
    private Quiz populateQuizObject(Map.Entry<String, List<ServiceResult>> e) {
        Quiz quiz = new Quiz();
        quiz.setCategory(e.getKey());
        List<QuizResult> quizResults = new ArrayList<>();
        e.getValue().forEach(entry -> quizResults.add(transformResult(entry)));
        quiz.setResults(quizResults);
        return quiz;
    }

    /**
     * transform service result object to quiz result object
     * @param serviceResult - Service result object
     * @return return the Quiz results objects
     */
    private QuizResult transformResult(ServiceResult serviceResult) {
        QuizResult quizResult = new QuizResult();
        quizResult.setCorrect_answer(serviceResult.getCorrect_answer());
        quizResult.setDifficulty(serviceResult.getDifficulty());
        quizResult.setType(serviceResult.getType());
        quizResult.setQuestion(serviceResult.getQuestion());
        quizResult.setAll_answers(serviceResult.getIncorrect_answers());
        quizResult.getAll_answers().add(serviceResult.getCorrect_answer());

        return quizResult;

}

}
