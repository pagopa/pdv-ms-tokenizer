package it.pagopa.pdv.tokenizer.connector.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev-local")
@ExtendWith(LocalDBCreationExtension.class)
@SpringBootTest(classes = {DaoTestConfig.class, TokenizerConnectorImpl.class})
class TokenizerConnectorImplTest {

    @Autowired
    private TokenizerConnectorImpl tokenizerConnector;

    @SpyBean
    private AmazonDynamoDB amazonDynamoDB;

    @SpyBean
    private DynamoDBMapper dynamoDBMapper;

    @BeforeEach
    void init() {
        DaoTestConfig.dynamoDBLocalSetup(amazonDynamoDB, dynamoDBMapper);
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
        TokenDto savedNewTokenDto = tokenizerConnector.save(pii, namespace);
        TokenDto savedExistingTokenDto = tokenizerConnector.save(pii, namespace);
        // then
        assertNotNull(savedNewTokenDto);
        assertNotNull(savedNewTokenDto.getRootToken());
        assertNotNull(savedNewTokenDto.getToken());
        assertEquals(savedNewTokenDto.getRootToken(), savedExistingTokenDto.getRootToken());
        assertEquals(savedNewTokenDto.getToken(), savedExistingTokenDto.getToken());
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
        Optional<TokenDto> found = tokenizerConnector.findById(pii, namespace);
        // then
        assertTrue(found.isEmpty());
    }


    @Test
    void findById() {
        // given
        String pii = "savedPii";
        String namespace = "savedSelfcare";
        TokenDto tokenDto = tokenizerConnector.save(pii, namespace);
        // when
        Optional<TokenDto> found = tokenizerConnector.findById(pii, namespace);
        // then
        assertTrue(found.isPresent());
        assertEquals(tokenDto.getRootToken(), found.get().getRootToken());
        assertEquals(tokenDto.getToken(), found.get().getToken());
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
        TokenDto tokenDto = tokenizerConnector.save(pii, namespace);
        // when
        Optional<String> found = tokenizerConnector.findPiiByToken(tokenDto.getRootToken(), GlobalFiscalCodeToken.NAMESPACE);
        // then
        assertTrue(found.isPresent());
        assertEquals(pii, found.get());
    }


    @Test
    void findPiiByToken_usingNamespacedToken() {
        // given
        String pii = "pii";
        String namespace = "selfcare";
        TokenDto tokenDto = tokenizerConnector.save(pii, namespace);
        // when
        Optional<String> found = tokenizerConnector.findPiiByToken(tokenDto.getToken(), namespace);
        // then
        assertTrue(found.isPresent());
        assertEquals(pii, found.get());
    }

    @Test
    void findPiiByToken_usingNotAllowedNamespacedToken() {
        // given
        String pii = "pii";
        String namespace = "selfcare";
        String notAllowedNamespace = "idpay";
        TokenDto tokenDto = tokenizerConnector.save(pii, namespace);
        // when
        Optional<String> found = tokenizerConnector.findPiiByToken(tokenDto.getToken(), notAllowedNamespace);
        // then
        assertFalse(found.isPresent());
    }

}