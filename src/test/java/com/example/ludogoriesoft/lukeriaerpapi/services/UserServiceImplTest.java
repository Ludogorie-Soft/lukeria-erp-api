package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AdminUserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.PublicUserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.RegisterRequest;
import com.example.ludogoriesoft.lukeriaerpapi.enums.Role;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserCreateException;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.UserNotFoundException;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.common.AccessDeniedException;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import com.example.ludogoriesoft.lukeriaerpapi.services.security.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password");
        request.setAddress("123 Main St");
        request.setUsername("johndoe");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstname(request.getFirstname());
        mockUser.setLastname(request.getLastname());
        mockUser.setEmail(request.getEmail());
        mockUser.setPassword("encodedPassword"); // Assuming password encoder returns this
        mockUser.setRole(Role.PRODUCTION_MANAGER);
        mockUser.setAddress(request.getAddress());
        mockUser.setUsernameField(request.getUsername());
        mockUser.setDeleted(false);

        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        User createdUser = userService.createUser(request);

        assertNotNull(createdUser);
        assertEquals(mockUser, createdUser);

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(request.getPassword());
    }

    @Test
    void createUser_DuplicateEmail_ThrowsUserCreateException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john.doe@example.com");

        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(UserCreateException.class, () -> userService.createUser(request));
    }
    @Test
    void testUpdateUser() {
        Long userId = 1L;
        AdminUserDTO userDTO = new AdminUserDTO();
        PublicUserDTO currentUser = new PublicUserDTO();
        User userToUpdate = new User();
        userToUpdate.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(), any())).thenReturn(new AdminUserDTO());
        AdminUserDTO result = userService.updateUser(userId, userDTO, currentUser);
        Assertions.assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));

    }
    @Test
    void testFindByEmail() {
        String userEmail = "test@example.com";
        User user = new User();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        User result = userService.findByEmail(userEmail);
        Assertions.assertNotNull(result);
        verify(userRepository).findByEmail(userEmail);
    }

    @Test
    void testFindByEmail_UserNotFound() {
        String userEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findByEmail(userEmail));
        verify(userRepository).findByEmail(userEmail);
    }
    @Test
    void testDeleteUserById_AccessDeniedException() {
        Long userId = 1L;
        PublicUserDTO currentUser = new PublicUserDTO();
        currentUser.setId(1L);
        User user = new User();
        user.setId(currentUser.getId());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertThrows(AccessDeniedException.class, () -> userService.deleteUserById(userId, currentUser));
        verify(userRepository).findById(userId);
    }
    @Test
    void getAllUsers_Success() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstname("John");
        mockUser.setLastname("Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setRole(Role.PRODUCTION_MANAGER);
        mockUser.setAddress("123 Main St");
        mockUser.setUsernameField("johndoe");
        mockUser.setDeleted(false);

        when(userRepository.findAll()).thenReturn(Collections.singletonList(mockUser));
        when(modelMapper.map(mockUser, AdminUserDTO.class)).thenReturn(new AdminUserDTO());

        List<AdminUserDTO> users = userService.getAllUsers();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());

        verify(userRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(mockUser, AdminUserDTO.class);
    }

    @Test
    void deleteUserById_Success() {
        Long userId = 1L;
        PublicUserDTO currentUser = new PublicUserDTO();
        currentUser.setId(2L);

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setDeleted(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        userService.deleteUserById(userId, currentUser);

        assertTrue(mockUser.isDeleted());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(mockUser);
    }

}


