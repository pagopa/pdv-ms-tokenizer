package it.pagopa.pdv.tokenizer.web.model.mapper;

import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import it.pagopa.pdv.tokenizer.web.model.TokenResource;

import java.util.UUID;

public class TokenResourceMapper {

    public static TokenResource from(TokenDto src) {
        TokenResource dest = new TokenResource();
        dest.setToken(UUID.fromString(src.getToken()));
        dest.setRootToken(UUID.fromString(src.getRootToken()));
        return dest;
    }

}
