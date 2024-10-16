package com.example.genealogie.controllers;

import com.example.genealogie.entities.ParcoursAcademique;
import com.example.genealogie.services.ParcoursAcaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academy")
public class AcademyController {
    private ParcoursAcaService academyService;

    @PostMapping("/")
    public ParcoursAcademique addAcademy(@RequestBody ParcoursAcademique academique){
        return academyService.save(academique);
    }

    @GetMapping("/profile/{profileId}")
    public List<ParcoursAcademique> getAcademicRecordsByProfile(@PathVariable Long profileId){
        return academyService.getParcoursByProfileId(profileId);
    }

    @DeleteMapping("/{id}")
    public void deleteAcademicRecord(@PathVariable Long id){
        academyService.delete(id);
    }
}
