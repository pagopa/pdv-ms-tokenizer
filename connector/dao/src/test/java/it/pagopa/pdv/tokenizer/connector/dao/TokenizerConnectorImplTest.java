package it.pagopa.pdv.tokenizer.connector.dao;

import it.pagopa.pdv.tokenizer.connector.dao.config.DaoTestConfig;
import it.pagopa.pdv.tokenizer.connector.dao.model.GlobalFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev-local")
@ExtendWith(LocalDBCreationExtension.class)
@SpringBootTest(classes = {DaoTestConfig.class, TokenizerConnectorImpl.class})
class TokenizerConnectorImplTest {

    @Autowired
    private TokenizerConnectorImpl tokenizerConnector;

    @SpyBean
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    @SpyBean
    private DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient;

    @BeforeEach
    void init() {
        DaoTestConfig.dynamoDBLocalSetup(dynamoDbAsyncClient, dbEnhancedAsyncClient);
    }

    @Test
    void save_nullPii() {
        // given
        String pii = null;
        String namespace = "selfcare";
        // when
        Executable executable = () -> tokenizerConnector.save(pii, namespace);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A Private Data is required", e.getMessage());
    }


    @Test
    void save_nullNamespace() {
        // given
        String pii = "pii";
        String namespace = null;
        // when
        Executable executable = () -> tokenizerConnector.save(pii, namespace);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A Namespace is required", e.getMessage());
    }


    @Test
    void save() {
        // given
        String pii = "savePii";
        String namespace = "saveSelfcare";
        // when
        tokenizerConnector.save(pii, namespace)
                .doOnSuccess(savedNewTokenDto ->
                        StepVerifier.create(tokenizerConnector.save(pii, namespace)
                                        .doOnNext(__ -> tokenizerConnector.save(pii, namespace)))
                                .assertNext(savedExistingTokenDto -> {
                                    // then
                                    assertNotNull(savedNewTokenDto);
                                    assertNotNull(savedNewTokenDto.getRootToken());
                                    assertNotNull(savedNewTokenDto.getToken());
                                    assertEquals(savedNewTokenDto.getRootToken(), savedExistingTokenDto.getRootToken());
                                    assertEquals(savedNewTokenDto.getToken(), savedExistingTokenDto.getToken());
                                })
                                .verifyComplete())
                .block();// FIXME: is the correct way to use a Publisher as an precondition for the test?
    }

    @Test
    void findById_nullPii() {
        // given
        String pii = null;
        String namespace = "selfcare";
        // when
        Executable executable = () -> tokenizerConnector.findById(pii, namespace);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A Private Data is required", e.getMessage());
    }


    @Test
    void findById_nullNamespace() {
        // given
        String pii = "pii";
        String namespace = null;
        // when
        Executable executable = () -> tokenizerConnector.findById(pii, namespace);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A Namespace is required", e.getMessage());
    }


    @Test
    void findById_notFound() {
        // given
        String pii = "piiNotFound";
        String namespace = "selfcare";
        // when
        Mono<TokenDto> found = tokenizerConnector.findById(pii, namespace);
        // then
        StepVerifier.create(found)
                .expectComplete();
    }


    @Test
    void findById() {
        // given
        String pii = "savedPii";
        String namespace = "savedSelfcare";
        // when
        tokenizerConnector.save(pii, namespace)
                .doOnSuccess(tokenDto ->
                        StepVerifier.create(tokenizerConnector.findById(pii, namespace))
                                .assertNext(found -> {
                                    // then
                                    assertNotNull(found);
                                    assertEquals(tokenDto.getRootToken(), found.getRootToken());
                                    assertEquals(tokenDto.getToken(), found.getToken());
                                })
                                .verifyComplete())
                .block();// FIXME: is the correct way to use a Publisher as an precondition for the test?
    }

    @Test
    void findPiiByToken_nullToken() {
        // given
        String token = null;
        String namespace = "namespace";
        // when
        Executable executable = () -> tokenizerConnector.findPiiByToken(token, namespace);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A token is required", e.getMessage());
    }


    @Test
    void findPiiByToken_usingGlobalToken() {
        // given
        String pii = "pii";
        String namespace = "selfcare";
        // when
        tokenizerConnector.save(pii, namespace)
                .doOnSuccess(tokenDto ->
                        StepVerifier.create(tokenizerConnector.findPiiByToken(tokenDto.getRootToken(), GlobalFiscalCodeToken.NAMESPACE))
                                .expectNext(pii)
                                .verifyComplete())
                .block();// FIXME: is the correct way to use a Publisher as an precondition for the test?
    }


    @Test
    void findPiiByToken_usingNamespacedToken() {
        // given
        String pii = "pii";
        String namespace = "selfcare";
        // when
        tokenizerConnector.save(pii, namespace)
                .doOnSuccess(tokenDto ->
                        StepVerifier.create(tokenizerConnector.findPiiByToken(tokenDto.getToken(), namespace))
                                .expectNext(pii)
                                .verifyComplete())
                .block();// FIXME: is the correct way to use a Publisher as an precondition for the test?
    }

    @Test
    void findPiiByToken_usingNotAllowedNamespacedToken() {
        // given
        String pii = "pii";
        String namespace = "selfcare";
        String notAllowedNamespace = "idpay";
        // when
        // then
        tokenizerConnector.save(pii, namespace)
                .doOnSuccess(tokenDto ->
                        StepVerifier.create(tokenizerConnector.findPiiByToken(tokenDto.getToken(), notAllowedNamespace))
                                .verifyComplete())
                .block();// FIXME: is the correct way to use a Publisher as an precondition for the test?
    }

}