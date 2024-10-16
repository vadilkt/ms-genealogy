package com.example.genealogie.services;

import com.example.genealogie.entities.ExperienceProfessionnelle;
import com.example.genealogie.entities.ParcoursAcademique;
import com.example.genealogie.repository.ParcoursAcademiqueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParcoursAcaService {

    private ParcoursAcademiqueRepository parcoursAcademiqueRepository;

    public ParcoursAcademique save(ParcoursAcademique p){
        return parcoursAcademiqueRepository.save(p);
    }

    public ParcoursAcademique getParcoursAcademiqueById(Long id){
        return parcoursAcademiqueRepository.findById(id)
                .orElseThrow(
                        ()->new RuntimeException("Le parcours académique correspondant à cet Id n'existe pas.")
                );
    }

    public List<ParcoursAcademique> getParcoursByProfileId(Long profileId){
        return parcoursAcademiqueRepository.findByProfileId(profileId);
    }

    public List<ParcoursAcademique> getParcoursAcademiques (){
        return parcoursAcademiqueRepository.findAll();
    }

    public ParcoursAcademique updateParcours(Long id, ParcoursAcademique newParcours){
        return parcoursAcademiqueRepository.findById(id)
                .map(parcours->{
                    parcours.setInstitution(newParcours.getInstitution());
                    parcours.setSpecialite(newParcours.getSpecialite());
                    parcours.setDiplome(newParcours.getDiplome());
                    parcours.setDateDebut(newParcours.getDateDebut());
                    parcours.setDateFin(newParcours.getDateFin());
                    return parcoursAcademiqueRepository.save(parcours);
                }).orElseThrow(
                        ()-> new RuntimeException("Le parcours académique correspondant à l'id "+ id+" n'existe pas")
                );
    }

    public void delete(Long id){
        parcoursAcademiqueRepository.deleteById(id);
    }
}
