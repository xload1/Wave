package com.example.wave.controllers;

import com.example.wave.entities.UserAccount;
import com.example.wave.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
@Validated
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping("/{id}/get-recommendations")
    public List<UserAccount> getUserRecommendations(@PathVariable Integer id){
        return recommendationService.getRecommendationList(id);
    }
}
