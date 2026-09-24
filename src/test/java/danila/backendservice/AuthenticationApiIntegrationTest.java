package danila.backendservice;

import danila.backendservice.dto.AuthRequestDto;
import danila.backendservice.dto.CurrentUserResponseDto;
import danila.backendservice.entity.User;
import danila.backendservice.repository.UserRepository;
import danila.backendservice.util.EndpointConstants;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class AuthenticationApiIntegrationTest {
    private static final String TEST_EMAIL = "test@gmail";
    private static final String TEST_PASSWORD = "12345";
    private static final String BEARER_SUBSTRING = "Bearer ";
    private static final String EMAIL_PARAM = "email";
    private static final String PASSWORD_PARAM = "password";
    private final UserRepository userRepository;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @BeforeEach
    void cleanDB() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("User register, if the credentials are valid and in json format")
    void mustRegisterWithJsonFormat() throws Exception {
        MvcResult mvcResultRegistration = performRegisterWithJsonFormat(TEST_EMAIL, TEST_PASSWORD);
        assertThat(mvcResultRegistration.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String authorizationHeader = mvcResultRegistration.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        assertThat(authorizationHeader).isNotNull().startsWith(BEARER_SUBSTRING);
        assertThat(userRepository.existsByEmail(TEST_EMAIL)).isTrue();
    }

    @Test
    @DisplayName("User register, if the credentials are valid and in form format")
    void mustRegisterWithFormFormat() throws Exception {
        MvcResult mvcResultRegistration = mockMvc.perform(MockMvcRequestBuilders
                .post(EndpointConstants.CURRENT_USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(EMAIL_PARAM, TEST_EMAIL)
                .param(PASSWORD_PARAM, TEST_PASSWORD)
        ).andReturn();
        assertThat(mvcResultRegistration.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String authorizationHeader = mvcResultRegistration.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        assertThat(authorizationHeader).isNotNull().startsWith(BEARER_SUBSTRING);
        assertThat(userRepository.existsByEmail(TEST_EMAIL)).isTrue();
    }

    @Test
    @DisplayName("After registration the user has access to protected endpoints")
    void mustGetAccessToProtectedEndpointsAfterRegistration() throws Exception {
        MvcResult mvcResultRegistration = performRegisterWithJsonFormat(TEST_EMAIL, TEST_PASSWORD);
        String authorizationHeader = mvcResultRegistration.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        MvcResult mvcResultCurrentUser = performGetCurrentUser(authorizationHeader);
        assertThat(mvcResultCurrentUser.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        CurrentUserResponseDto currentUserResponseDto = objectMapper.readValue(mvcResultCurrentUser
                .getResponse().getContentAsString(), CurrentUserResponseDto.class);
        assertThat(currentUserResponseDto.email()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("Upon registration, if user with this email already exist,return 409 code without Jwt")
    void mustReturnConflictCodeWithoutJwtWhenRegister() throws Exception {
        userRepository.save(new User(TEST_EMAIL, passwordEncoder.encode(TEST_PASSWORD)));
        MvcResult mvcResultRegistration = performRegisterWithJsonFormat(TEST_EMAIL, TEST_PASSWORD);
        assertThat(mvcResultRegistration.getResponse().getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        String authorizationHeader = mvcResultRegistration.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        assertThat(authorizationHeader).isNull();
    }

    @Test
    @DisplayName("User login, if the credentials are valid and in form format")
    void mustLoginWithFormFormat() throws Exception {
        userRepository.save(new User(TEST_EMAIL, passwordEncoder.encode(TEST_PASSWORD)));
        MvcResult mvcResultLogin = mockMvc.perform(MockMvcRequestBuilders
                .post(EndpointConstants.LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(EMAIL_PARAM, TEST_EMAIL)
                .param(PASSWORD_PARAM, TEST_PASSWORD)
        ).andReturn();
        assertThat(mvcResultLogin.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String authorizationHeader = mvcResultLogin.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        assertThat(authorizationHeader).isNotNull().startsWith(BEARER_SUBSTRING);
    }

    @Test
    @DisplayName("User login, if the credentials are valid and in json format")
    void mustLoginWithJsonFormat() throws Exception {
        userRepository.save(new User(TEST_EMAIL, passwordEncoder.encode(TEST_PASSWORD)));
        MvcResult mvcResultLogin = performLoginWithJsonFormat(TEST_EMAIL, TEST_PASSWORD);
        assertThat(mvcResultLogin.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String authorizationHeader = mvcResultLogin.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        assertThat(authorizationHeader).isNotNull().startsWith(BEARER_SUBSTRING);
    }

    @Test
    @DisplayName("Upon login, if user with this email not exist,return 401 code without Jwt")
    void mustReturnUnauthorizedCodeWithoutJwtWhenLogin() throws Exception {
        MvcResult mvcResultRegistration = performLoginWithJsonFormat(TEST_EMAIL, TEST_PASSWORD);
        assertThat(mvcResultRegistration.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        String authorizationHeader = mvcResultRegistration.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        assertThat(authorizationHeader).isNull();
    }

    @Test
    @DisplayName("After login the user has access to protected endpoints")
    void mustGetAccessToProtectedEndpointsAfterLogin() throws Exception {
        userRepository.save(new User(TEST_EMAIL, passwordEncoder.encode(TEST_PASSWORD)));
        MvcResult mvcResultRegistration = performLoginWithJsonFormat(TEST_EMAIL, TEST_PASSWORD);
        String authorizationHeader = mvcResultRegistration.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        MvcResult mvcResultCurrentUser = performGetCurrentUser(authorizationHeader);
        assertThat(mvcResultCurrentUser.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        CurrentUserResponseDto currentUserResponseDto = objectMapper.readValue(mvcResultCurrentUser
                .getResponse().getContentAsString(), CurrentUserResponseDto.class);
        assertThat(currentUserResponseDto.email()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("Upon try get access in protected endpoints without Jwt,return 401 code")
    void mustReturnUnauthorizedCodeWhenGetCurrentUserWithoutJwt() throws Exception {
        MvcResult mvcResultCurrentUser = performGetCurrentUser("");
        assertThat(mvcResultCurrentUser.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Upon try get access in protected endpoints with wrong Jwt,return 401 code")
    void mustReturnUnauthorizedCodeWhenGetCurrentUserWithWrongJwt() throws Exception {
        MvcResult mvcResultCurrentUser = performGetCurrentUser("Bearer wrongJwt");
        assertThat(mvcResultCurrentUser.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Upon try get access in protected endpoints with expired Jwt,return 401 code")
    void mustReturnUnauthorizedCodeWhenGetCurrentUserWithExpiredJwt() throws Exception {
        MvcResult mvcResultRegistration = performRegisterWithJsonFormat(TEST_EMAIL, TEST_PASSWORD);
        String authorizationHeader = mvcResultRegistration.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        Thread.sleep(2000);
        MvcResult mvcResultCurrentUser = performGetCurrentUser(authorizationHeader);
        assertThat(mvcResultCurrentUser.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private MvcResult performRegisterWithJsonFormat(String email, String password) throws Exception {
        AuthRequestDto requestRegistration = new AuthRequestDto(email, password);
        String json = objectMapper.writeValueAsString(requestRegistration);
        return mockMvc.perform(MockMvcRequestBuilders
                .post(EndpointConstants.CURRENT_USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andReturn();
    }

    private MvcResult performLoginWithJsonFormat(String email, String password) throws Exception {
        AuthRequestDto requestRegistration = new AuthRequestDto(email, password);
        String json = objectMapper.writeValueAsString(requestRegistration);
        return mockMvc.perform(MockMvcRequestBuilders
                .post(EndpointConstants.LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andReturn();
    }

    private MvcResult performGetCurrentUser(String authorizationHeader) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get(EndpointConstants.CURRENT_USER_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
        ).andReturn();
    }
}
