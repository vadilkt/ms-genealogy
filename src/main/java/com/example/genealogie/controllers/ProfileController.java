package com.example.genealogie.controllers;

import com.example.genealogie.entities.Profile;
import com.example.genealogie.services.ProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private ProfileService profileService;

    @GetMapping("/{id}")
    public Profile getProfile(@PathVariable Long id){
         return profileService.findUserById(id);
    }

    @GetMapping("/search")
    public List<Profile> getProfiles(@RequestBody String searchInput){
        return profileService.search(searchInput);
    }

    @PostMapping("/")
    public Profile createProfile(@RequestBody Profile profile){
        return  profileService.save(profile);
    }

    @PutMapping("/{id}")
    public Profile updateProfile(@PathVariable Long id, @RequestBody Profile profile){
        return profileService.update(id, profile);
    }

    @DeleteMapping("/{id}")
    public void deleteProfile(@PathVariable Long id){
        profileService.deleteProfile(id);
    }


}
