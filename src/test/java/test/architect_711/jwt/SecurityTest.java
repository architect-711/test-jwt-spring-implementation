package test.architect_711.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import test.architect_711.jwt.model.dto.PersonDto;
import test.architect_711.jwt.model.dto.TokenDto;
import test.architect_711.jwt.model.entity.Role;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // REQUIRED! because otherwise each method will erase resultTokens variable
public class SecurityTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    // Stores returned tokens from login request
    private TokenDto resultTokens;

    /**
     * Should get 401 Unauthorized status, since makes request to protected endpoint without JWT
     * */
    @Test
    public void should_fail_on_restricted_endpoint_without_token() throws Exception {
        mockMvc.perform(get("/people/username")).andExpect(status().isUnauthorized()).andDo(print());
    }

    /**
     * Should successfully make GET request to unprotected endpoint.
     * */
    @Test
    public void should_return_greeting_on_public_endpoint() throws Exception {
        mockMvc.perform(get("/people/hello")).andExpect(status().isOk()).andDo(print());
    }

    /**
     * Should successfully make POST request to save new person. The person object should not have an `id` and `role` hence they are assigned by app
     * */
    @Test
    public void should_save_new_person_and_tokens() throws Exception {
        mockMvc.
                perform(
                        post("/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(new PersonDto(null, "test", "test", "test", null)))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    /**
     * Should successfully make login request (send created Person JSON object) and get tokens in return
     * */
    @Test
    public void should_login() throws Exception {
        String payload = mapper.writeValueAsString(new PersonDto(1L, "test", "test", "test", Role.USER));
        String response = mockMvc
                .perform(
                    post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString();

        resultTokens = convertResponse(response);
    }

    /**
     * Should get person's username from SecurityContext based on provided access token, also the endpoint is restricted
     * */
    @Test
    public void should_return_username_on_restricted_endpoint() throws Exception {
        mockMvc
                .perform(get("/people/username")
                        .header("Authorization", "Bearer " + resultTokens.getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /**
     * Should successfully get new tokens based on provided refresh token and make previous ones logged out
     * */
    @Test
    public void should_update_tokens() throws Exception {
        Thread.sleep(2_000); // REQUIRED! because otherwise the same tokens will be generated, since generation time of previous tokens equals to new ones
        String response = mockMvc
                .perform(post("/refresh_tokens")
                        .header("Authorization", "Bearer " + resultTokens.getRefreshToken()))
                .andExpect(status().isOk())
                .andDo(print()).andReturn().getResponse().getContentAsString();

        TokenDto updatedTokens = convertResponse(response);

        assertNotEquals(resultTokens.getRefreshToken(), updatedTokens.getRefreshToken());
    }

    @Test
    public void should_fail_on_invalid_token() throws Exception {
        mockMvc
                .perform(
                        get("/people/username")
                                .header("Authorization", "hava nagila")
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private TokenDto convertResponse(String response) throws JsonProcessingException {
        return mapper.readValue(response, TokenDto.class);
    }
}
