package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.Role;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiRequestException;
import com.example.ludogoriesoft.lukeriaerpapi.mappers.UserMapper;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    void testToDTO() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");

        UserDTO expectedDto = new UserDTO();
        expectedDto.setId(1L);
        expectedDto.setUsername("john_doe");

        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDTO dto = userService.toDTO(user);

        assertEquals(expectedDto.getId(), dto.getId());
        assertEquals(expectedDto.getUsername(), dto.getUsername());

        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testToEntity() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("john_doe");

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("john_doe");

        when(userMapper.toEntity(dto)).thenReturn(expectedUser);

        User user = userService.toEntity(dto);

        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getUsername(), user.getUsername());

        verify(userMapper, times(1)).toEntity(dto);
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("john_doe");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("alice_smith");

        List<User> userList = Arrays.asList(user1, user2);

        UserDTO dto1 = new UserDTO();
        dto1.setId(1L);
        dto1.setUsername("john_doe");

        UserDTO dto2 = new UserDTO();
        dto2.setId(2L);
        dto2.setUsername("alice_smith");

        List<UserDTO> expectedDtoList = Arrays.asList(dto1, dto2);

        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toDto(user1)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);

        List<UserDTO> dtoList = userService.getAllUsers();

        assertEquals(expectedDtoList.size(), dtoList.size());
        for (int i = 0; i < expectedDtoList.size(); i++) {
            UserDTO expectedDto = expectedDtoList.get(i);
            UserDTO dto = dtoList.get(i);
            assertEquals(expectedDto.getId(), dto.getId());
            assertEquals(expectedDto.getUsername(), dto.getUsername());
        }

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDto(user1);
        verify(userMapper, times(1)).toDto(user2);
    }

    @Test
    void testGetUserById_ExistingUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("john_doe");

        UserDTO expectedDto = new UserDTO();
        expectedDto.setId(userId);
        expectedDto.setUsername("john_doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDTO dto = userService.getUserById(userId);

        assertEquals(expectedDto.getId(), dto.getId());
        assertEquals(expectedDto.getUsername(), dto.getUsername());

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testGetUserById_NonExistingUser() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ApiRequestException.class, () -> userService.getUserById(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testCreateUser_ValidUser() {
        User user = new User();
        user.setUsername("john_doe");

        UserDTO expectedDto = new UserDTO();
        expectedDto.setUsername("john_doe");

        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDTO dto = userService.createUser(user);

        assertEquals(expectedDto.getUsername(), dto.getUsername());

        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testCreateUser_BlankUsername() {
        User user = new User();
        user.setUsername("");

        assertThrows(ApiRequestException.class, () -> userService.createUser(user));

        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testUpdateUserWithoutPassword_ExistingUser() {
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("john_doe");

        UserDTO userDto = new UserDTO();
        userDto.setUsername("john_doe_updated");
        userDto.setFullName("John Doe Updated");
        userDto.setEmail("john_doe@example.com");
        userDto.setRole(Role.ADMIN);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("john_doe_updated");
        updatedUser.setFullName("John Doe Updated");
        updatedUser.setEmail("john_doe@example.com");
        updatedUser.setRole(Role.ADMIN);

        UserDTO expectedDto = new UserDTO();
        expectedDto.setId(userId);
        expectedDto.setUsername("john_doe_updated");
        expectedDto.setFullName("John Doe Updated");
        expectedDto.setEmail("john_doe@example.com");
        expectedDto.setRole(Role.ADMIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(expectedDto);

        UserDTO dto = userService.updateUserWithoutPassword(userId, userDto);

        assertEquals(expectedDto.getId(), dto.getId());
        assertEquals(expectedDto.getUsername(), dto.getUsername());
        assertEquals(expectedDto.getFullName(), dto.getFullName());
        assertEquals(expectedDto.getEmail(), dto.getEmail());
        assertEquals(expectedDto.getRole(), dto.getRole());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
        verify(userMapper, times(1)).toDto(updatedUser);
    }

    @Test
    void testUpdateUserWithoutPassword_NonExistingUser() {
        Long userId = 1L;

        UserDTO userDto = new UserDTO();
        userDto.setUsername("john_doe_updated");
        userDto.setFullName("John Doe Updated");
        userDto.setEmail("john_doe@example.com");
        userDto.setRole(Role.ADMIN);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ApiRequestException.class, () -> userService.updateUserWithoutPassword(userId, userDto));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testUpdateUserWithoutPassword_InvalidUserData() {
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("john_doe");

        UserDTO userDto = new UserDTO();
        userDto.setUsername(null);
        userDto.setFullName(null);
        userDto.setEmail(null);
        userDto.setRole(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(ApiRequestException.class, () -> userService.updateUserWithoutPassword(userId, userDto));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testDeleteUser_ExistingUser() {
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("john_doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    void testDeleteUser_NonExistingUser() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ApiRequestException.class, () -> userService.deleteUser(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).delete(any());
    }
}
