package it.pagopa.pdv.tokenizer.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pdv.tokenizer.TestUtils;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import it.pagopa.pdv.tokenizer.core.TokenizerService;
import it.pagopa.pdv.tokenizer.web.config.WebTestConfig;
import it.pagopa.pdv.tokenizer.web.handler.RestExceptionsHandler;
import it.pagopa.pdv.tokenizer.web.model.PiiResource;
import it.pagopa.pdv.tokenizer.web.model.TokenResource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Autowired
    protected ObjectMapper objectMapper;


    @Test
    void save() throws Exception {
        // given
        String namespace = "selfcare";
        PiiResource piiResource = TestUtils.mockInstance(new PiiResource());
        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(UUID.randomUUID().toString());
        tokenDto.setRootToken(UUID.randomUUID().toString());
        Mockito.when(tokenizerServiceMock.save(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(tokenDto);
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .put(BASE_URL + "/")
                .header(NAMESPACE_HEADER_NAME, namespace)
                .content(objectMapper.writeValueAsString(piiResource))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        // then
        TokenResource tokenResource = objectMapper.readValue(result.getResponse().getContentAsString(), TokenResource.class);
        assertNotNull(tokenResource);
        Mockito.verify(tokenizerServiceMock, Mockito.times(1))
                .save(piiResource.getPii(), namespace);
        Mockito.verifyNoMoreInteractions(tokenizerServiceMock);
    }


    @Test
    void search() throws Exception {
        String namespace = "selfcare";
        PiiResource piiResource = TestUtils.mockInstance(new PiiResource());
        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(UUID.randomUUID().toString());
        tokenDto.setRootToken(UUID.randomUUID().toString());
        Mockito.when(tokenizerServiceMock.findById(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(tokenDto);
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post(BASE_URL + "/search")
                .header(NAMESPACE_HEADER_NAME, namespace)
                .content(objectMapper.writeValueAsString(piiResource))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        // then
        TokenResource tokenResource = objectMapper.readValue(result.getResponse().getContentAsString(), TokenResource.class);
        assertNotNull(tokenResource);
        Mockito.verify(tokenizerServiceMock, Mockito.times(1))
                .findById(piiResource.getPii(), namespace);
        Mockito.verifyNoMoreInteractions(tokenizerServiceMock);
    }


    @Test
    void findPii() throws Exception {
        // given
        UUID token = UUID.randomUUID();
        String pii = "pii";
        Mockito.when(tokenizerServiceMock.findPiiByToken(Mockito.anyString()))
                .thenReturn(pii);
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/{token}/pii", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        // then
        PiiResource piiResource = objectMapper.readValue(result.getResponse().getContentAsString(), PiiResource.class);
        assertNotNull(piiResource);
        Mockito.verify(tokenizerServiceMock, Mockito.times(1))
                .findPiiByToken(token.toString());
        Mockito.verifyNoMoreInteractions(tokenizerServiceMock);
    }

}