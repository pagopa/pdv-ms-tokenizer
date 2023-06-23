package it.pagopa.pdv.tokenizer.web.model.mapper;

import it.pagopa.pdv.tokenizer.web.model.PiiResource;

public class PiiResourceMapper {

    public static PiiResource from(String pii) {
        PiiResource dest = new PiiResource();
        dest.setPii(pii);
        return dest;
    }

}
