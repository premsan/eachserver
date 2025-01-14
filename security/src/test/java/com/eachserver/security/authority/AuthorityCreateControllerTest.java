package com.eachserver.security.authority;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthorityCreateControllerTest {

    @Autowired private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = {"SECURITY_AUTHORITY_CREATE"})
    void getAuthorityCreate() throws Exception {

        mockMvc.perform(get("/security/authority-create"))
                .andExpectAll(status().isOk(), content().contentType("text/html;charset=UTF-8"));
    }
}
