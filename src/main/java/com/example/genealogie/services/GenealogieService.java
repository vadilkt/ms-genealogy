package com.example.genealogie.services;

import com.example.genealogie.entities.Profile;
import com.example.genealogie.entities.RelationFamille;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenealogieService {

    private ProfileService profileService;
    private RelationFamilleService relationFamilleService;

    public Profile getProfileWithFamily(Long id){
        Profile profile  = profileService.findUserById(id);
        List<RelationFamille> relations = relationFamilleService.getRelationsByProfileId(id);
        profile.setRelationFamilles(relations);
        return profile;
    }
}
