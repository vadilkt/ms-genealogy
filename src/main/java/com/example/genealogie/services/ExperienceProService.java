package com.example.genealogie.services;

import com.example.genealogie.entities.ExperienceProfessionnelle;
import com.example.genealogie.repository.ExperienceProfessionnelRepository;
import com.example.genealogie.util.ProfileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExperienceProService {

    ExperienceProfessionnelRepository experienceProfessionnelRepository;

    public ExperienceProfessionnelle save(ExperienceProfessionnelle exp){
        return experienceProfessionnelRepository.save(exp);
    }

    public List<ExperienceProfessionnelle> getExperiencesByProfileId(Long profileId){
        return experienceProfessionnelRepository.findByProfileId(profileId);
    }

    public void deleteExp(Long id){
        experienceProfessionnelRepository.deleteById(id);
    }

    public ExperienceProfessionnelle updateXP(Long id, ExperienceProfessionnelle exp){
        return experienceProfessionnelRepository.findById(id)
                .map(xp->{
                    xp.setEntreprise(exp.getEntreprise());
                    xp.setPoste(exp.getPoste());
                    xp.setDateDebut(exp.getDateDebut());
                    xp.setDateFin(exp.getDateFin());
                    return experienceProfessionnelRepository.save(xp);
                }).orElseThrow(()->new RuntimeException("Expérience professionnelle avec l'id "+id+" non trouvée."));
    }
}
