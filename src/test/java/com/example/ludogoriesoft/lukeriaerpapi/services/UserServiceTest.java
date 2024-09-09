package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.Role;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserNotFoundException;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User existingUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsernameField("existingUser");
        existingUser.setDeleted(false);
        // Set other properties as needed
    }

    @Test
    void getAllUsers() {
        when(userRepository.findByDeletedFalse()).thenReturn(Collections.singletonList(existingUser));
        when(modelMapper.map(existingUser, UserDTO.class)).thenReturn(new UserDTO());
        List<UserDTO> userDTOList = userService.getAllUsers();
        assertFalse(userDTOList.isEmpty());
        assertEquals(1, userDTOList.size());
    }

    @Test
    void getUserById_userExists() throws UserNotFoundException, ChangeSetPersister.NotFoundException {
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingUser));
        when(modelMapper.map(existingUser, UserDTO.class)).thenReturn(new UserDTO());
        UserDTO userDTO = userService.getUserById(1L);
        assertNotNull(userDTO);
        assertEquals(existingUser.getUsername(), userDTO.getUsername());
    }

    @Test
    void findByEmail_userExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
        User user = userService.findByEmail("test@example.com");
        assertNotNull(user);
    }

    @Test
    void findByEmail_userNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findByEmail("nonexistent@example.com"));
    }

    @Test
    void updateUser_validUser() throws ChangeSetPersister.NotFoundException {
        // Setup initial data
        User existingUser = new User(1L, "bel", "bel", "bel@gmail.com", "pass", "address", "user", Role.ADMIN, false);
        UserDTO userDTO = new UserDTO(1L, "bel", "bel", "bel@gmail.com", "pass", "pass", "address", "user", Role.ADMIN);
        User updatedUser = new User(1L, "bel", "bel", "bel@gmail.com", "pass", "address", "user", Role.ADMIN, false);

        // Mock repository behavior
        when(userRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Mock modelMapper behavior
        when(modelMapper.map(any(UserDTO.class), any())).thenReturn(updatedUser);
        when(modelMapper.map(any(User.class), any())).thenReturn(userDTO);

        // Call the service method
        UserDTO result = userService.updateUser(1L, userDTO);

        // Validate the results
        assertEquals(userDTO.getId(), result.getId());
        assertEquals(userDTO.getUsername(), result.getUsername());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getAddress(), result.getAddress());
        assertEquals(userDTO.getRole(), result.getRole());
    }

    @Test
    void updateUser_userNotFound() {
        when(userRepository.findByIdAndDeletedFalse(2L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> userService.updateUser(2L, new UserDTO()));
    }

    @Test
    void createUser_NullUsername_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void createUser_NullFirstName_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void createUser_NullLastName_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setFirstname("lastName");
        assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void createUser_NullAddress_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setFirstname("lastName");
        userDTO.setLastname("lastName");
        assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void createUser_NullEmail_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setFirstname("lastName");
        userDTO.setLastname("lastName");
        userDTO.setAddress("address");
        assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void createUser_NullPassword_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setFirstname("lastName");
        userDTO.setLastname("lastName");
        userDTO.setAddress("address");
        userDTO.setEmail("email@gmail.com");
        assertThrows(ValidationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void updateUser_NullUsername_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        User user = new User();
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void updateUser_NullAddress_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        User user = new User();
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void updateUser_NullFirstName_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setAddress("address");
        User user = new User();
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void updateUser_NullLastName_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setAddress("address");
        userDTO.setFirstname("firstName");
        User user = new User();
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void updateUser_NullEmail_ValidationExceptionThrown() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setAddress("address");
        userDTO.setFirstname("firstName");
        userDTO.setLastname("lastname");
        User user = new User();
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void deleteUser_UserExists_UserDeletedSuccessfully() {
        Long userId = 1L;
        User userToDelete = new User();
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(userToDelete));

        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository, times(1)).save(userToDelete);
        assertTrue(userToDelete.isDeleted());
    }

    @Test
    void deleteUser_UserNotFound_ThrowsNotFoundException() {
        Long userId = 1L;
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).save(any(User.class));
    }
}
