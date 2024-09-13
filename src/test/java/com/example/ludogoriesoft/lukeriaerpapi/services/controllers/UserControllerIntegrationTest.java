package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.UserController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AuthenticationResponse;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.PublicUserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.services.UserService;
import com.example.ludogoriesoft.lukeriaerpapi.slack.SlackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = UserController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = UserController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = SlackService.class)
        }
)
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SlackService slackService;

    @MockBean
    private UserService userService;

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setUsername("User 1");
        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setUsername("User 2");
        List<UserDTO> userDTOList = Arrays.asList(userDTO1, userDTO2);

        when(userService.getAllUsers()).thenReturn(userDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("User 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("User 2"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testGetUserById() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("User 1");

        when(userService.getUserById(anyLong())).thenReturn(userDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("User 1"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testCreateUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("New User");

        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"username\": \"New User\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("New User"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("Updated User");

        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenReturn(userDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/{id}", 1)
                        .content("{\"id\": 1, \"username\": \"Updated User\"}")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("Updated User"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testDeleteUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User with id: 1 has been deleted successfully!"));
    }

    @Test
    void testGetAllUsersWhenNoUserExist() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();
    }

    @Test
    void testGetUserByIdWhenUserDoesNotExist() throws Exception {
        long userId = 1L;
        when(userService.getUserById(userId)).thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testShouldNotCreateUserWithBlankUserName() throws Exception {
        String blankUserName = "";

        doThrow(new ValidationException())
                .when(userService).createUser(any(UserDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user")
                        .content("{\"id\": 1, \"username\": \"" + blankUserName + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    void testGetUserWithInvalidId() throws Exception {
        Long invalidId = 100000L;

        when(userService.getUserById(invalidId))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/{id}", invalidId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testUpdateUserWithInvalidData() throws Exception {
        String invalidData = "";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/{id}", 1)
                        .content("{\"id\": 1, \"username\": " + invalidData + "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateUserWithInvalidIdShouldReturnNotFound() throws Exception {
        Long id = 1L;

        UserDTO updatedUser = new UserDTO();

        when(userService.updateUser(eq(id), any(UserDTO.class)))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUserByIdWhenUserDoesNotExist() throws Exception {
        long userId = 1L;
        doThrow(new ChangeSetPersister.NotFoundException())
                .when(userService).deleteUser(userId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testRestoreUserNotFound() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setDeleted(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/restore/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    void testFindAuthenticatedUser_Success() throws Exception {
        // Create a UserDTO object to be returned by the service
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("authenticated@example.com");
        userDTO.setUsername("authenticatedUser");

        // Mock the behavior of the userService
        when(userService.findAuthenticatedUser()).thenReturn(userDTO);

        // Perform the GET request to /api/v1/user/me
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(jsonPath("$.id").value(1)) // Verify user ID
                .andExpect(jsonPath("$.email").value("authenticated@example.com")) // Verify user email
                .andExpect(jsonPath("$.username").value("authenticatedUser")) // Verify username
                .andReturn();
    }
    @Test
    void testIfPassMatch_Success() throws Exception {
        // Mock the behavior of the userService
        when(userService.ifPasswordMatch(anyString())).thenReturn(true);

        // Perform the GET request to /api/v1/user/ifPassMatch with a password parameter
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/ifPassMatch")
                        .param("password", "password123") // Add the password param
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(content().string("true")) // Expect "true" as the response
                .andReturn();
    }
    @Test
    void testIfPassMatch_PasswordMismatch() throws Exception {
        // Mock the behavior of the userService to return false (password doesn't match)
        when(userService.ifPasswordMatch(anyString())).thenReturn(false);

        // Perform the GET request with a mismatched password
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/ifPassMatch")
                        .param("password", "wrongPassword") // Add a wrong password
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(content().string("false")) // Expect "false" as the response
                .andReturn();
    }
    @Test
    void testChangePassword_Success() throws Exception {
        // Create a UserDTO object to simulate the request payload
        UserDTO userDTO = new UserDTO();
        userDTO.setPassword("newPassword123");

        // Mock the behavior of the userService
        when(userService.updatePassword(any(UserDTO.class))).thenReturn(true);

        // Perform the PUT request to /api/v1/user/change-pass
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/change-pass")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTO))) // Use helper method to convert object to JSON
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(content().string("true")) // Expect "true" as the response
                .andReturn();
    }
    @Test
    void testChangePassword_InvalidPasswordFormat() throws Exception {
        // Create a UserDTO object with an invalid password
        UserDTO userDTO = new UserDTO();
        userDTO.setPassword("short"); // Password too short

        // Mock the behavior of the userService to throw a ValidationException
        doThrow(new ValidationException("Password is too short")).when(userService).updatePassword(any(UserDTO.class));

        // Perform the PUT request with the invalid password
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/change-pass")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTO))) // Use helper method to convert object to JSON
                .andExpect(status().isBadRequest()) // Expect 400 Bad Request
                .andExpect(content().string(containsString("Password is too short"))) // Expect error message
                .andReturn();
    }
    @Test
    void testUpdateAuthenticatedUser_Success() throws Exception {
        Long userId = 1L;

        // Create a UserDTO object with valid data
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("updated@example.com");
        userDTO.setUsername("updatedUser");
        userDTO.setAddress("123 Test Street");
        userDTO.setFirstname("Updated");
        userDTO.setLastname("User");
        userDTO.setPassword("newPassword123");

        // Mock the AuthenticationResponse returned by the service
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .accessToken("newAccessToken")
                .refreshToken("newRefreshToken")
                .user(new PublicUserDTO()) // Mock the mapped PublicUserDTO
                .build();

        // Mock the behavior of the userService
        when(userService.updateAuthenticateUser(eq(userId), any(UserDTO.class))).thenReturn(authenticationResponse);

        // Perform the PUT request to /api/v1/user/authenticated/{id}
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/authenticated/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTO))) // Use helper method to convert object to JSON
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(jsonPath("$.accessToken").value("newAccessToken")) // Verify accessToken
                .andExpect(jsonPath("$.refreshToken").value("newRefreshToken")) // Verify refreshToken
                .andReturn();
    }
    @Test
    void testUpdateAuthenticatedUser_UserNotFound() throws Exception {
        Long userId = 1L;

        // Create a UserDTO object with valid data
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("updated@example.com");
        userDTO.setUsername("updatedUser");
        userDTO.setAddress("123 Test Street");
        userDTO.setFirstname("Updated");
        userDTO.setLastname("User");
        userDTO.setPassword("newPassword123");

        // Mock the behavior of the userService to throw a NotFoundException
        when(userService.updateAuthenticateUser(eq(userId), any(UserDTO.class)))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        // Perform the PUT request to /api/v1/user/authenticated/{id} with a non-existing user
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/authenticated/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTO))) // Use helper method to convert object to JSON
                .andExpect(status().isNotFound()) // Expect 404 Not Found
                .andExpect(content().string(containsString("Not found!"))) // Expect error message
                .andReturn();
    }
    @Test
    void testForgotPassword_ValidEmail() throws Exception {
        when(userService.processForgotPassword(anyString())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/forgot-password")
                        .param("email", "valid@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(content().string("Password reset link sent to your email."))
                .andReturn();
    }

    @Test
    void testForgotPassword_InvalidEmail() throws Exception {
        when(userService.processForgotPassword(anyString())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/forgot-password")
                        .param("email", "invalid@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email address."))
                .andReturn();
    }

    @Test
    void testResetPassword_ValidToken() throws Exception {
        when(userService.updatePasswordWithToken(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/reset-password")
                        .param("token", "valid-token")
                        .param("password", "newValidPassword123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andReturn();
    }

    @Test
    void testResetPassword_InvalidToken() throws Exception {
        // Mock the service to return false for an invalid token
        when(userService.updatePasswordWithToken(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/reset-password")
                        .param("token", "invalid-token")
                        .param("password", "newValidPassword123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"))
                .andReturn();
    }

}
