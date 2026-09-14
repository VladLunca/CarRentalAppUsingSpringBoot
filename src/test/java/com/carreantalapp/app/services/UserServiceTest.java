package com.carreantalapp.app.services;

import com.carreantalapp.app.dto.ProfileEditDto;
import com.carreantalapp.app.dto.WebUserDTO;
import com.carreantalapp.app.model.User;
import com.carreantalapp.app.model.UserDetails;
import com.carreantalapp.app.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user(Long id, String username, String encodedPassword) {
        User user = new User(
                username,
                encodedPassword,
                new UserDetails("Ana", "Pop", "ana@gmail.com", "1234567890123", "0712345678"));
        user.setId(id);
        return user;
    }

    private ProfileEditDto profileDto(String username, String currentPassword, String newPassword) {
        ProfileEditDto dto = new ProfileEditDto();
        dto.setUserName(username);
        dto.setCurrentPassword(currentPassword);
        dto.setNewPassword(newPassword);
        dto.setFirstName("Ana-Maria");
        dto.setLastName("Popescu");
        dto.setEmail("ana.new@gmail.com");
        dto.setPhoneNumber("0799999999");
        return dto;
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        void storesEncodedPasswordNeverThePlainOne() {
            WebUserDTO dto = new WebUserDTO();
            dto.setUserName("ana");
            dto.setPassword("plaintext123");
            dto.setFirstName("Ana");
            dto.setLastName("Pop");
            dto.setEmail("ana@gmail.com");
            dto.setCnp("1234567890123");
            dto.setPhoneNumber("0712345678");
            when(passwordEncoder.encode("plaintext123")).thenReturn("{bcrypt}hashed");

            userService.register(dto);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            User saved = captor.getValue();

            assertThat(saved.getPassword()).isEqualTo("{bcrypt}hashed");
            assertThat(saved.getPassword()).isNotEqualTo("plaintext123");
            assertThat(saved.getUsername()).isEqualTo("ana");
            assertThat(saved.getUserDetails().getEmail()).isEqualTo("ana@gmail.com");
            assertThat(saved.getUserDetails().getCnp()).isEqualTo("1234567890123");
        }
    }

    @Nested
    @DisplayName("usernameExists")
    class UsernameExists {

        @Test
        void trueWhenFound() {
            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(user(1L, "ana", "x")));

            assertThat(userService.usernameExists("ana")).isTrue();
        }

        @Test
        void falseWhenMissing() {
            when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

            assertThat(userService.usernameExists("ghost")).isFalse();
        }
    }

    @Nested
    @DisplayName("getProfileEditDto")
    class GetProfileEditDto {

        @Test
        void copiesCurrentValuesIntoTheForm() {
            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(user(1L, "ana", "x")));

            ProfileEditDto dto = userService.getProfileEditDto("ana");

            assertThat(dto.getUserName()).isEqualTo("ana");
            assertThat(dto.getFirstName()).isEqualTo("Ana");
            assertThat(dto.getEmail()).isEqualTo("ana@gmail.com");
            assertThat(dto.getCnp()).isEqualTo("1234567890123");
            assertThat(dto.getPhoneNumber()).isEqualTo("0712345678");
        }

        @Test
        void rejectsUnknownUser() {
            when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getProfileEditDto("ghost"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");
        }
    }

    @Nested
    @DisplayName("updateProfile")
    class UpdateProfile {

        @Test
        void rejectsWrongCurrentPassword() {
            User existing = user(1L, "ana", "{bcrypt}stored");
            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(existing));
            when(passwordEncoder.matches("wrong", "{bcrypt}stored")).thenReturn(false);

            assertThatThrownBy(() -> userService.updateProfile("ana", profileDto("ana", "wrong", null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Incorrect current password");
            verify(userRepository, never()).save(any());
        }

        @Test
        void rejectsUsernameAlreadyTaken() {
            User existing = user(1L, "ana", "{bcrypt}stored");
            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(existing));
            when(userRepository.findByUsername("dan")).thenReturn(Optional.of(user(2L, "dan", "y")));
            when(passwordEncoder.matches("secret", "{bcrypt}stored")).thenReturn(true);

            assertThatThrownBy(() -> userService.updateProfile("ana", profileDto("dan", "secret", null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already taken");
            verify(userRepository, never()).save(any());
        }

        @Test
        void rejectsNewPasswordShorterThanSixCharacters() {
            User existing = user(1L, "ana", "{bcrypt}stored");
            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(existing));
            when(passwordEncoder.matches("secret", "{bcrypt}stored")).thenReturn(true);

            assertThatThrownBy(() -> userService.updateProfile("ana", profileDto("ana", "secret", "12345")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("at least 6 characters");
            verify(userRepository, never()).save(any());
        }

        @Test
        void updatesDetailsAndReturnsFalseWhenNothingSensitiveChanged() {
            User existing = user(1L, "ana", "{bcrypt}stored");
            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(existing));
            when(passwordEncoder.matches("secret", "{bcrypt}stored")).thenReturn(true);

            boolean needsReLogin = userService.updateProfile("ana", profileDto("ana", "secret", null));

            assertThat(needsReLogin).isFalse();
            assertThat(existing.getUserDetails().getFirstName()).isEqualTo("Ana-Maria");
            assertThat(existing.getUserDetails().getLastName()).isEqualTo("Popescu");
            assertThat(existing.getUserDetails().getEmail()).isEqualTo("ana.new@gmail.com");
            assertThat(existing.getUserDetails().getPhoneNumber()).isEqualTo("0799999999");
            assertThat(existing.getPassword()).isEqualTo("{bcrypt}stored");
            verify(userRepository).save(existing);
        }

        @Test
        void blankNewPasswordLeavesPasswordUntouched() {
            User existing = user(1L, "ana", "{bcrypt}stored");
            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(existing));
            when(passwordEncoder.matches("secret", "{bcrypt}stored")).thenReturn(true);

            boolean needsReLogin = userService.updateProfile("ana", profileDto("ana", "secret", "   "));

            assertThat(needsReLogin).isFalse();
            assertThat(existing.getPassword()).isEqualTo("{bcrypt}stored");
            verify(passwordEncoder, never()).encode(any());
        }

        @Test
        void returnsTrueWhenPasswordChanged() {
            User existing = user(1L, "ana", "{bcrypt}stored");
            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(existing));
            when(passwordEncoder.matches("secret", "{bcrypt}stored")).thenReturn(true);
            when(passwordEncoder.encode("brandNewPass")).thenReturn("{bcrypt}newhash");

            boolean needsReLogin = userService.updateProfile("ana", profileDto("ana", "secret", "brandNewPass"));

            assertThat(needsReLogin).isTrue();
            assertThat(existing.getPassword()).isEqualTo("{bcrypt}newhash");
        }

        @Test
        void returnsTrueWhenUsernameChanged() {
            User existing = user(1L, "ana", "{bcrypt}stored");
            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(existing));
            when(userRepository.findByUsername("ana2")).thenReturn(Optional.empty());
            when(passwordEncoder.matches("secret", "{bcrypt}stored")).thenReturn(true);

            boolean needsReLogin = userService.updateProfile("ana", profileDto("ana2", "secret", null));

            assertThat(needsReLogin).isTrue();
            assertThat(existing.getUsername()).isEqualTo("ana2");
        }

        @Test
        void rejectsUnknownUser() {
            when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateProfile("ghost", profileDto("ghost", "secret", null)))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");
        }
    }

    @Nested
    @DisplayName("removeUser")
    class RemoveUser {

        @Test
        void deletesExistingUserAndReturnsTrue() {
            when(userRepository.findByUsername("ana")).thenReturn(Optional.of(user(1L, "ana", "x")));

            assertThat(userService.removeUser("ana")).isTrue();

            verify(userRepository).deleteUserById(1L);
        }

        @Test
        void returnsFalseForUnknownUser() {
            when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

            assertThat(userService.removeUser("ghost")).isFalse();

            verify(userRepository, never()).deleteUserById(anyLong());
        }
    }
}
