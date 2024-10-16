package com.example.genealogie.services;

import com.example.genealogie.entities.RelationFamille;
import com.example.genealogie.repository.RelationFamilleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelationFamilleService {

    private RelationFamilleRepository relationFamilleRepository;

    public List<RelationFamille> getRelationsByProfileId(Long id){
        return relationFamilleRepository.findByProfileId(id);
    }

    public List<RelationFamille> getRelationsByRelativeId(Long id){
        return relationFamilleRepository.findByRelativeId(id);
    }

}
