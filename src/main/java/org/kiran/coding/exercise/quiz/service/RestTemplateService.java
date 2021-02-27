package org.kiran.coding.exercise.quiz.service;

import org.kiran.coding.exercise.quiz.exception.QuizRequestAPIException;
import org.kiran.coding.exercise.quiz.model.Root;
import org.kiran.coding.exercise.quiz.model.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class RestTemplateService {

    private final RestTemplate restTemplate;

    public RestTemplateService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    /**
     * Calling the external service
     * @param url - service url
     * @return return the Service results objects
     */
    public List<ServiceResult> invokeService(String url)  {
        List<ServiceResult> serviceResults = new ArrayList<>();
        try {
            ResponseEntity<Root> response = restTemplate.getForEntity(url, Root.class);
            if(response.hasBody()){
                if(response.getBody() != null && !CollectionUtils.isEmpty(response.getBody().getResults())){
                    return response.getBody().getResults();
                }
            }
        }catch (RestClientException exception){
            throw new QuizRequestAPIException("Rest service exception occurred "+ exception.getMessage());
        }
        return serviceResults;
    }
}
