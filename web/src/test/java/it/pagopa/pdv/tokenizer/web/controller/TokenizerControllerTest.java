package it.pagopa.pdv.tokenizer.web.controller;

import com.jayway.jsonpath.JsonPath;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import it.pagopa.pdv.tokenizer.core.TokenizerService;
import it.pagopa.pdv.tokenizer.web.config.WebTestConfig;
import it.pagopa.pdv.tokenizer.web.handler.RestExceptionsHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(value = {TokenizerController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        TokenizerController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class
})
class TokenizerControllerTest {

    private static final String BASE_URL = "/tokens";
    private static final String NAMESPACE_HEADER_NAME = "x-pagopa-namespace";

    @MockBean
    private TokenizerService tokenizerServiceMock;

    @Autowired
    protected MockMvc mvc;


    @Test
    void save(@Value("classpath:stubs/piiResource.json") Resource piiResource) throws Exception {
        // given
        String namespace = "selfcare";
        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(UUID.randomUUID().toString());
        tokenDto.setRootToken(UUID.randomUUID().toString());
        Mockito.when(tokenizerServiceMock.save(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(tokenDto);
        // when
        mvc.perform(MockMvcRequestBuilders
                .put(BASE_URL)
                .header(NAMESPACE_HEADER_NAME, namespace)
                .content(piiResource.getInputStream().readAllBytes())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.rootToken", notNullValue()));
        // then
        String stubbedPii = JsonPath.parse(piiResource.getInputStream()).read("$.pii", String.class);
        Mockito.verify(tokenizerServiceMock, Mockito.times(1))
                .save(stubbedPii, namespace);
        Mockito.verifyNoMoreInteractions(tokenizerServiceMock);
    }


    @Test
    void search(@Value("classpath:stubs/piiResource.json") Resource piiResource) throws Exception {
        String namespace = "selfcare";
        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(UUID.randomUUID().toString());
        tokenDto.setRootToken(UUID.randomUUID().toString());
        Mockito.when(tokenizerServiceMock.findById(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(tokenDto);
        // when
        mvc.perform(MockMvcRequestBuilders
                .post(BASE_URL + "/search")
                .header(NAMESPACE_HEADER_NAME, namespace)
                .content(piiResource.getInputStream().readAllBytes())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.rootToken", notNullValue()));
        // then
        String stubbedPii = JsonPath.parse(piiResource.getInputStream()).read("$.pii", String.class);
        Mockito.verify(tokenizerServiceMock, Mockito.times(1))
                .findById(stubbedPii, namespace);
        Mockito.verifyNoMoreInteractions(tokenizerServiceMock);
    }


    @Test
    void findPii() throws Exception {
        // given
        UUID token = UUID.randomUUID();
        String pii = "pii";
        String namespace = "namespace";
        Mockito.when(tokenizerServiceMock.findPiiByToken(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(pii);
        // when
        mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/{token}/pii", token)
                        .header(NAMESPACE_HEADER_NAME, namespace)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.pii", notNullValue()));
        // then
        Mockito.verify(tokenizerServiceMock, Mockito.times(1))
                .findPiiByToken(token.toString(), namespace);
        Mockito.verifyNoMoreInteractions(tokenizerServiceMock);
    }

}