package com.carreantalapp.app.services;

import com.carreantalapp.app.dto.WebUserDTO;
import com.carreantalapp.app.model.User;
import com.carreantalapp.app.model.UserDetails;
import com.carreantalapp.app.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public void register(@Valid WebUserDTO webUserDTO) {

        UserDetails  userDetails = new UserDetails(webUserDTO.getFirstName(),
                webUserDTO.getLastName(),
                webUserDTO.getEmail(),
                webUserDTO.getCnp(),
                webUserDTO.getPhoneNumber() );
        User  user =  new User(webUserDTO.getUserName(), passwordEncoder.encode(webUserDTO.getPassword()), userDetails);
        userRepository.save(user);
    }
    public boolean usernameExists(String userName) {
        return userRepository.findByUsername(userName).isPresent();
    }
    @Transactional
    public boolean deleteUser(String username) {
        if(usernameExists(username)){
            if(userRepository.findByUsername(username).isPresent()){
                userRepository.deleteUserById(userRepository.findByUsername(username).get().getId());
            }
            else {
                return false;
            }
        }else {
            return false;
        }
        return true;
    }
}
