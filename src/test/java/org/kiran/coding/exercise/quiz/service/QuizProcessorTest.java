package org.kiran.coding.exercise.quiz.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kiran.coding.exercise.quiz.exception.QuizRequestAPIException;
import org.kiran.coding.exercise.quiz.model.QuizData;
import org.kiran.coding.exercise.quiz.model.ServiceResult;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class QuizProcessorTest {

    @Mock
    private RestTemplateService restTemplateService;

    @InjectMocks
    private QuizProcessor quizProcessor;


    @Test
    public void processRequestTest(){
        List<ServiceResult> result = new ArrayList<>();
        when(restTemplateService.invokeService(any())).thenReturn(result);
        ResponseEntity<QuizData> responseEntity = quizProcessor.processRequest();
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(restTemplateService,times(2)).invokeService(any());

    }

    @Test
    public void processRequest_BodyTest(){
        List<ServiceResult> result = new ArrayList<>();
        result.add(buildServiceRequest());
        when(restTemplateService.invokeService(any())).thenReturn(result);
        ResponseEntity<QuizData> responseEntity = quizProcessor.processRequest();
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getQuiz().get(0).getCategory()).isEqualTo("Entertainment: Film");
        verify(restTemplateService,times(2)).invokeService(any());
    }

    @Test
    public void processRequest_ErrorTest(){
        when(restTemplateService.invokeService(any())).thenThrow(new RestClientException("Service unavailable"));
        ResponseEntity<QuizData> responseEntity = quizProcessor.processRequest();
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(restTemplateService,times(1)).invokeService(any());
    }

    @Test
    public void processRequest_Error1Test(){
        when(restTemplateService.invokeService(any())).thenThrow(new QuizRequestAPIException("Service unavailable"));
        ResponseEntity<QuizData> responseEntity = quizProcessor.processRequest();
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        verify(restTemplateService,times(1)).invokeService(any());
    }

    public ServiceResult buildServiceRequest(){
        ServiceResult serviceResult = new ServiceResult();
        serviceResult.setDifficulty("easy");
        serviceResult.setCorrect_answer("Uma Thurman");
        serviceResult.setQuestion("Which actress danced the twist with John Travolta in Pulp Fiction?");
        serviceResult.setCategory("Entertainment: Film");
        List<String> answers = new ArrayList<>();
        answers.add("Kathy Griffin");
        answers.add("Pam Grier");
        answers.add("Bridget Fonda");
        serviceResult.setIncorrect_answers(answers);

        return serviceResult;
    }

}
