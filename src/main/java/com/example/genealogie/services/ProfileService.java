package com.example.genealogie.services;

import com.example.genealogie.repository.ProfileRepository;
import com.example.genealogie.util.ProfileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.genealogie.entities.Profile;

import java.util.List;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    public Profile save(Profile profile){
        return profileRepository.save(profile);
    }

    public List<Profile> search(String searchInput){
            List<Profile> profiles = profileRepository.findByNomOrPrenom(searchInput, searchInput);
            if(profiles ==null || profiles.isEmpty()){
                throw new ProfileNotFoundException("Le profile avec le nom ou le prenom "+ searchInput+" n'a pas été trouvé !");
            }
            return profiles;
    }

    public Profile update(Long id, Profile newProfile){
        return profileRepository.findById(id)
                .map(profile->{
                    profile.setNom(profile.getNom());
                    profile.setPrenom(profile.getPrenom());
                    profile.setDateNaissance(profile.getDateNaissance());
                    profile.setDateMort(profile.getDateMort());
                    profile.setPays(profile.getPays());
                    profile.setVille(profile.getVille());
                    profile.setTelephone(profile.getTelephone());
                    profile.setExperienceProfessionnelles(profile.getExperienceProfessionnelles());
                    profile.setParcoursAcademiques(profile.getParcoursAcademiques());
                    return profileRepository.save(profile);
                })
                .orElseThrow(()->new ProfileNotFoundException("Le profil avec l'ID "+ id+" n'existe pas !"));
    }

    public Profile findUserById(Long id){
        return profileRepository.findById(id).orElseThrow(()->new ProfileNotFoundException("Ce profil n'existe pas !"));
    }

    public List<Profile> profiles (){
        return profileRepository.findAll();
    }

    public void deleteProfile(Long id){
       if(profileRepository.existsById(id)){
           profileRepository.deleteById(id);
       }else{
           throw new ProfileNotFoundException("Profil avec l'ID "+id+" non trouvé.");
       }
    }
}
