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
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@WebFluxTest(value = {TokenizerController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
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
    private WebTestClient webTestClient;


    @Test
    void save(@Value("classpath:stubs/piiResource.json") Resource piiResource) throws Exception {
        // given
        String namespace = "selfcare";
        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(UUID.randomUUID().toString());
        tokenDto.setRootToken(UUID.randomUUID().toString());
        Mockito.when(tokenizerServiceMock.save(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.just(tokenDto));
        // when
        webTestClient.put()
                .uri(BASE_URL + "/")
                .header(NAMESPACE_HEADER_NAME, namespace)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(piiResource.getInputStream().readAllBytes())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isNotEmpty()
                .jsonPath("$.rootToken").isNotEmpty();
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
                .thenReturn(Mono.just(tokenDto));
        // when
        webTestClient.post()
                .uri(BASE_URL + "/search")
                .header(NAMESPACE_HEADER_NAME, namespace)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(piiResource.getInputStream().readAllBytes())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isNotEmpty()
                .jsonPath("$.rootToken").isNotEmpty();
        // then
        String stubbedPii = JsonPath.parse(piiResource.getInputStream()).read("$.pii", String.class);
        Mockito.verify(tokenizerServiceMock, Mockito.times(1))
                .findById(stubbedPii, namespace);
        Mockito.verifyNoMoreInteractions(tokenizerServiceMock);
    }


    @Test
    void findPii() {
        // given
        UUID token = UUID.randomUUID();
        String pii = "pii";
        String namespace = "namespace";
        Mockito.when(tokenizerServiceMock.findPiiByToken(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.just(pii));
        // when
        webTestClient.get()
                .uri(BASE_URL + "/{token}/pii", token)
                .header(NAMESPACE_HEADER_NAME, namespace)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pii").isNotEmpty();
        // then
        Mockito.verify(tokenizerServiceMock, Mockito.times(1))
                .findPiiByToken(token.toString(), namespace);
        Mockito.verifyNoMoreInteractions(tokenizerServiceMock);
    }

}