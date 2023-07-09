package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.Role;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.ValidationException;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

 class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
     void testGetAllUsers() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setFullName("User 1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setFullName("User 2");

        List<User> mockUsers = Arrays.asList(user1, user2);
        when(userRepository.findByDeletedFalse()).thenReturn(mockUsers);

        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setUsername("user1");
        userDTO1.setFullName("User 1");

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setUsername("user2");
        userDTO2.setFullName("User 2");

        when(modelMapper.map(user1, UserDTO.class)).thenReturn(userDTO1);
        when(modelMapper.map(user2, UserDTO.class)).thenReturn(userDTO2);

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertEquals(mockUsers.size(), result.size());
        assertEquals(mockUsers.get(0).getUsername(), result.get(0).getUsername());
        assertEquals(mockUsers.get(0).getFullName(), result.get(0).getFullName());
        assertEquals(mockUsers.get(1).getUsername(), result.get(1).getUsername());
        assertEquals(mockUsers.get(1).getFullName(), result.get(1).getFullName());
    }

    @Test
    void testGetUserById_ExistingUser() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("user1");
        user.setFullName("User 1");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("user1");
        userDTO.setFullName("User 1");

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        // Act
        UserDTO result = userService.getUserById(userId);

        // Assert
        assertEquals(userId, result.getId());
        assertEquals("user1", result.getUsername());
        assertEquals("User 1", result.getFullName());
    }

    @Test
    void testGetUserById_NonExistingUser() {
        Long userId = 1L;

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            userService.getUserById(userId);
        });
    }

    @Test
    void testCreateUser_ValidUser() throws ValidationException {
        // Arrange
        User user = new User();
        user.setUsername("user1");
        user.setFullName("User 1");
        user.setEmail("user1@example.com");
        user.setPassword("password");
        user.setRole(Role.ADMIN);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("user1");
        userDTO.setFullName("User 1");
        userDTO.setEmail("user1@example.com");
        userDTO.setRole(Role.ADMIN);

        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        // Act
        UserDTO result = userService.createUser(user);

        // Assert
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getFullName(), result.getFullName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getRole(), result.getRole());

        verify(userRepository).save(user);
    }


    @Test
     void testDeleteUser_ExistingUser() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("user1");
        user.setFullName("User 1");
        user.setDeleted(false);

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(userId);

        // Assert
        assertEquals(true, user.isDeleted());
        verify(userRepository).save(user);
    }

    @Test
     void testDeleteUser_NonExistingUser() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            userService.deleteUser(userId);
        });
    }

    @Test
     void testRestoreUser_ExistingUser() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("user1");
        user.setFullName("User 1");
        user.setDeleted(true);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("user1");
        userDTO.setFullName("User 1");

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        // Act
        UserDTO result = userService.restoreUser(userId);

        // Assert
        assertEquals(false, user.isDeleted());
        assertEquals(userId, result.getId());
        assertEquals("user1", result.getUsername());
        assertEquals("User 1", result.getFullName());
        verify(userRepository).save(user);
    }

    @Test
     void testRestoreUser_NonExistingUser() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            userService.restoreUser(userId);
        });
    }


    @Test
    void testUpdateUser_ValidUser() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("user1");
        existingUser.setFullName("User 1");
        existingUser.setEmail("user1@example.com");
        existingUser.setRole(Role.ADMIN);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("updatedUser1");
        userDTO.setFullName("Updated User 1");
        userDTO.setEmail("updateduser1@example.com");
        userDTO.setRole(Role.ADMIN);

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(existingUser));
        when(modelMapper.map(existingUser, UserDTO.class)).thenReturn(userDTO);

        // Act
        User updatedUser = new User(); // Създаване на обект updatedUser
        updatedUser.setId(userId); // Задаване на id
        when(userRepository.save(existingUser)).thenReturn(updatedUser); // Мокване на save(), връщайки updatedUser
        UserDTO result = userService.updateUser(userId, userDTO);


        verify(userRepository).save(existingUser);
    }

    @Test
    void testUpdateUser_InvalidUser_UsernameBlank() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("user1");
        existingUser.setFullName("User 1");
        existingUser.setEmail("user1@example.com");
        existingUser.setRole(Role.ADMIN);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(""); // Празно потребителско име

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(existingUser));

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> {
            userService.updateUser(userId, userDTO);
        });
    }

     @Test
     void testUpdateUser_InvalidUser_FullNameBlank() {
         // Arrange
         Long userId = 1L;
         User existingUser = new User();
         existingUser.setId(userId);
         existingUser.setUsername("user1");
         existingUser.setFullName("User 1");
         existingUser.setEmail("user1@example.com");
         existingUser.setRole(Role.ADMIN);

         UserDTO userDTO = new UserDTO();
         userDTO.setUsername("user1");
         userDTO.setFullName(""); // Празно потребителско име

         when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(existingUser));

         // Act and Assert
         assertThrows(jakarta.validation.ValidationException.class, () -> {
             userService.updateUser(userId, userDTO);
         });
     }

     @Test
     void testUpdateUser_InvalidUser_EmailBlank() {
         // Arrange
         Long userId = 1L;
         User existingUser = new User();
         existingUser.setId(userId);
         existingUser.setUsername("user1");
         existingUser.setFullName("User 1");
         existingUser.setEmail("");
         existingUser.setRole(Role.ADMIN);

         UserDTO userDTO = new UserDTO();
         userDTO.setUsername("user1");
         userDTO.setUsername("user user 1 ");
         userDTO.setEmail(""); // Празно потребителско име

         when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(existingUser));

         // Act and Assert
         assertThrows(jakarta.validation.ValidationException.class, () -> {
             userService.updateUser(userId, userDTO);
         });
     }





    @Test
    void testCreateUser_UsernameBlank() {
        // Arrange
        User user = new User();
        user.setUsername(""); // Празно потребителско име
        user.setFullName("User 1");
        user.setEmail("user1@example.com");
        user.setPassword("password");
        user.setRole(Role.ADMIN);

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> {
            userService.createUser(user);
        });
    }

    @Test
    void testCreateUser_FullNameBlank() {
        // Arrange
        User user = new User();
        user.setUsername("User 1"); // Празно потребителско име
        user.setFullName("");
        user.setEmail("user1@example.com");
        user.setPassword("password");
        user.setRole(Role.ADMIN);

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> {
            userService.createUser(user);
        });
    }

    @Test
    void testCreateUser_EmailBlank() {
        // Arrange
        User user = new User();
        user.setUsername("User 1"); // Празно потребителско име
        user.setFullName("User User");
        user.setEmail("");
        user.setPassword("password");
        user.setRole(Role.ADMIN);

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> {
            userService.createUser(user);
        });
    }

    @Test
    void testCreateUser_PasswordBlank() {
        // Arrange
        User user = new User();
        user.setUsername("User 1"); // Празно потребителско име
        user.setFullName("User User");
        user.setEmail("mail@user.com");
        user.setPassword("");
        user.setRole(Role.ADMIN);

        // Act and Assert
        assertThrows(jakarta.validation.ValidationException.class, () -> {
            userService.createUser(user);
        });
    }


}


