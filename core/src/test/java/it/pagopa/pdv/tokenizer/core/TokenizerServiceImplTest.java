package it.pagopa.pdv.tokenizer.core;

import it.pagopa.pdv.tokenizer.connector.TokenizerConnector;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import it.pagopa.pdv.tokenizer.core.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenizerServiceImplTest {

    @InjectMocks
    private TokenizerServiceImpl tokenizerService;

    @Mock
    private TokenizerConnector tokenizerConnector;


    @Test
    void save_nullPii() {
        // given
        String pii = null;
        String namespace = "selfcare";
        // when
        Executable executable = () -> tokenizerService.save(pii, namespace);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A Private Data is required", e.getMessage());
        Mockito.verifyNoInteractions(tokenizerConnector);
    }


    @Test
    void save_nullNamespace() {
        // given
        String pii = "pii";
        String namespace = null;
        // when
        Executable executable = () -> tokenizerService.save(pii, namespace);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A Namespace is required", e.getMessage());
        Mockito.verifyNoInteractions(tokenizerConnector);
    }


    @Test
    void save() {
        // given
        String pii = "pii";
        String namespace = "selfcare";
        TokenDto tokenDtoStub = new TokenDto();
        Mockito.when(tokenizerConnector.save(Mockito.any(), Mockito.any()))
                .thenReturn(tokenDtoStub);
        // when
        TokenDto tokenDto = tokenizerService.save(pii, namespace);
        // then
        assertSame(tokenDtoStub, tokenDto);
        Mockito.verify(tokenizerConnector, Mockito.times(1))
                .save(pii, namespace);
        Mockito.verifyNoMoreInteractions(tokenizerConnector);
    }


    @Test
    void findById_nullPii() {
        // given
        String pii = null;
        String namespace = "selfcare";
        // when
        Executable executable = () -> tokenizerService.findById(pii, namespace);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A Private Data is required", e.getMessage());
        Mockito.verifyNoInteractions(tokenizerConnector);
    }


    @Test
    void findById_nullNamespace() {
        // given
        String pii = "pii";
        String namespace = null;
        // when
        Executable executable = () -> tokenizerService.findById(pii, namespace);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A Namespace is required", e.getMessage());
        Mockito.verifyNoInteractions(tokenizerConnector);
    }


    @Test
    void findById_notFound() {
        // given
        String pii = "pii";
        String namespace = "selfcare";
        Mockito.when(tokenizerConnector.findById(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.empty());
        // when
        Executable executable = () -> tokenizerService.findById(pii, namespace);
        // then
        assertThrows(ResourceNotFoundException.class, executable);
        Mockito.verify(tokenizerConnector, Mockito.times(1))
                .findById(pii, namespace);
        Mockito.verifyNoMoreInteractions(tokenizerConnector);
    }


    @Test
    void findById() {
        // given
        String pii = "pii";
        String namespace = "selfcare";
        TokenDto tokenDtoStub = new TokenDto();
        Mockito.when(tokenizerConnector.findById(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(tokenDtoStub));
        // when
        TokenDto tokenDto = tokenizerService.findById(pii, namespace);
        // then
        assertSame(tokenDtoStub, tokenDto);
        Mockito.verify(tokenizerConnector, Mockito.times(1))
                .findById(pii, namespace);
        Mockito.verifyNoMoreInteractions(tokenizerConnector);
    }


    @Test
    void findPiiByToken_nullToken() {
        // given
        String token = null;
        String namespace = "namespace";
        // when
        Executable executable = () -> tokenizerService.findPiiByToken(token, namespace);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A token is required", e.getMessage());
        Mockito.verifyNoInteractions(tokenizerConnector);
    }


    @Test
    void findPiiByToken_notFound() {
        // given
        String token = "token";
        String namespace = "namespace";
        Mockito.when(tokenizerConnector.findPiiByToken(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.empty());
        // when
        Executable executable = () -> tokenizerService.findPiiByToken(token, namespace);
        // then
        assertThrows(ResourceNotFoundException.class, executable);
        Mockito.verify(tokenizerConnector, Mockito.times(1))
                .findPiiByToken(token, namespace);
        Mockito.verifyNoMoreInteractions(tokenizerConnector);
    }


    @Test
    void findPiiByToken() {
        // given
        String token = "token";
        String piiStub = "pii";
        String namespace = "namespace";

        Mockito.when(tokenizerConnector.findPiiByToken(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(piiStub));
        // when
        String pii = tokenizerService.findPiiByToken(token, namespace);
        // then
        assertSame(piiStub, pii);
        Mockito.verify(tokenizerConnector, Mockito.times(1))
                .findPiiByToken(token, namespace);
        Mockito.verifyNoMoreInteractions(tokenizerConnector);
    }

}