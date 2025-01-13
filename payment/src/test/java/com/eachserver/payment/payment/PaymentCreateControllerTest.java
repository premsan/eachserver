package com.eachserver.payment.payment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentCreateControllerTest {

    @Autowired private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = {"PAYMENT_PAYMENT_CREATE"})
    void getPaymentCreate() throws Exception {

        mockMvc.perform(get("/payment/payment-create"))
                .andExpectAll(status().isOk(), content().contentType("text/html;charset=UTF-8"));
    }
}
