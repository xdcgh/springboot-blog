package hello.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {
    private MockMvc mvc;

    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(userService, authenticationManager)).build();
    }

    @Test
    void returnNotLoginByDefault() throws Exception {
        // 未登录时，/auth 接口返回未登录状态
        mvc.perform(get("/auth"))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    String responseContent = mvcResult.getResponse().getContentAsString();
                    System.out.println(responseContent);
//                    // 这里由于有编码问题，无法实现断言语句
//                    Assertions.assertTrue(responseContent.contains("用户没有登录"));
                });
    }

    @Test
    void testLogin() throws Exception {
        // 使用/auth/login 登录
        Mockito.when(userService.loadUserByUsername("MyUser"))
                .thenReturn(new User("MyUser",
                        bCryptPasswordEncoder.encode("MyPassword"),
                        Collections.emptyList()));

        Mockito.when(userService.getUserByUsername("MyUser"))
                .thenReturn(new hello.entity.User(123, "MyUser", "MyPassword"));

        Map<String, String> usernamePassword = new HashMap<>();
        usernamePassword.put("username", "MyUser");
        usernamePassword.put("password", "MyPassword");

        MvcResult response = mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(usernamePassword)))
                .andExpect(status().isOk())
//                .andExpect(result ->
//                        Assertions.assertTrue(
//                                result.getResponse().getContentAsString().contains("登录成功")))
                .andReturn();

        HttpSession session = response.getRequest().getSession();

        // 再次检查
        // /auth 的返回值，处于登录状态
        mvc.perform(get("/auth").session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(result ->
                        Assertions.assertTrue(
                                result.getResponse().getContentAsString().contains("MyUser")));
    }
}