package com.carreantalapp.app.services;

import com.carreantalapp.app.dto.ProfileEditDto;
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

    @Transactional(readOnly = true)
    public ProfileEditDto getProfileEditDto(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        ProfileEditDto dto = new ProfileEditDto();
        dto.setUserName(user.getUsername());
        dto.setFirstName(user.getUserDetails().getFirstName());
        dto.setLastName(user.getUserDetails().getLastName());
        dto.setEmail(user.getUserDetails().getEmail());
        dto.setPhoneNumber(user.getUserDetails().getPhoneNumber());
        dto.setCnp(user.getUserDetails().getCnp());
        return dto;
    }

    @Transactional
    public boolean updateProfile(String currentUsername, ProfileEditDto dto) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + currentUsername));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password.");
        }

        boolean usernameChanged = !dto.getUserName().equals(currentUsername);
        if (usernameChanged && usernameExists(dto.getUserName())) {
            throw new IllegalArgumentException("Username '" + dto.getUserName() + "' is already taken.");
        }

        user.setUsername(dto.getUserName());

        boolean passwordChanged = dto.getNewPassword() != null && !dto.getNewPassword().isBlank();
        if (passwordChanged) {
            if (dto.getNewPassword().length() < 6) {
                throw new IllegalArgumentException("New password must be at least 6 characters.");
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        UserDetails details = user.getUserDetails();
        details.setFirstName(dto.getFirstName());
        details.setLastName(dto.getLastName());
        details.setEmail(dto.getEmail());
        details.setPhoneNumber(dto.getPhoneNumber());

        userRepository.save(user);
        return passwordChanged || usernameChanged;
    }
    @Transactional
    public boolean removeUser(String username) {
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
