package org.kiran.coding.exercise.quiz.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kiran.coding.exercise.quiz.exception.QuizRequestAPIException;
import org.kiran.coding.exercise.quiz.model.Root;
import org.kiran.coding.exercise.quiz.model.ServiceResult;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class RestTemplateServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestTemplateService restTemplateService;

    @Test
    public void invokeServiceTest(){
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
        ReflectionTestUtils.setField(response,"body", new Root());
        when(restTemplate.getForEntity(anyString(),any())).thenReturn(response);
        List<ServiceResult> serviceResults = restTemplateService.invokeService("/sample/url");

        assertThat(serviceResults).isNotNull();
        assertThat(serviceResults.size()).isEqualTo(0);

        verify(restTemplate,times(1)).getForEntity(anyString(),any());
    }

    @Test
    public void invokeServiceTest_Exception(){
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
        ReflectionTestUtils.setField(response,"body", new Root());
        when(restTemplate.getForEntity(anyString(),any())).thenThrow(new RestClientException("service call Exception"));
       // List<ServiceResult> serviceResults = restTemplateService.invokeService("/sample/url");
        Throwable result = catchThrowable(() -> restTemplateService.invokeService("/sample/url"));
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(QuizRequestAPIException.class).hasMessage("Rest service exception occurred service call Exception");

        verify(restTemplate,times(1)).getForEntity(anyString(),any());
    }


}
