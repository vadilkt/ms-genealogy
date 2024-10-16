package com.example.genealogie.controllers;

import com.example.genealogie.entities.ExperienceProfessionnelle;
import com.example.genealogie.services.ExperienceProService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experience")
public class ExperienceProfessionnelleController {
    private ExperienceProService experienceProService;

    @PostMapping("/")
    public ExperienceProfessionnelle addExperience(@RequestBody ExperienceProfessionnelle experienceProfessionnelle){
        return experienceProService.save(experienceProfessionnelle);
    }

    @GetMapping("/profile/{profileId}")
    public List<ExperienceProfessionnelle> getXp(@PathVariable Long profileId){
        return experienceProService.getExperiencesByProfileId(profileId);
    }

    @PutMapping("/{id}")
    public ExperienceProfessionnelle updateXp(@RequestBody ExperienceProfessionnelle xp, @PathVariable Long id){
        return experienceProService.updateXP(id,xp);
    }

    @DeleteMapping("/{id}")
    public void deleteExperience(@PathVariable Long id){
        experienceProService.deleteExp(id);
    }
}
