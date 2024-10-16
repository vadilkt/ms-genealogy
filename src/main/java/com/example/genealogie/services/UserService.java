package com.example.genealogie.services;

import com.example.genealogie.entities.Role;
import com.example.genealogie.entities.TypeDeRole;
import com.example.genealogie.entities.User;
import com.example.genealogie.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User save (User user){
        Role role = new Role();
        role.setLibelle(TypeDeRole.UTILISATEUR);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findByUsername (String username){
        User user=userRepository.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("Utilisateur introuvable avec le nom d'utilisateur: "+username);
        }
        return  user;
    }

    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userRepository.findByUsername(username);
        if(user==null){
            logger.error("Utilisateur avec le nom d'utilisateur '{}' introuvable", username);
            throw new UsernameNotFoundException("Utilisateur introuvable avec le nom d'utilisateur: "+ username);
        }
        return user;
    }

}
