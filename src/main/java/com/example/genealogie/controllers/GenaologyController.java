package com.example.genealogie.controllers;

import com.example.genealogie.entities.Profile;
import com.example.genealogie.services.GenealogieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/genealogy")
public class GenaologyController {
    private GenealogieService genealogieService;

    @GetMapping("/tree/{profileId}")
    public Profile getGenealogyTree(@PathVariable Long profileId){
        return genealogieService.getProfileWithFamily(profileId);
    }
}
