package it.pagopa.pdv.tokenizer.connector.dao.model;


import lombok.Data;
import lombok.experimental.FieldNameConstants;
import software.amazon.awssdk.enhanced.dynamodb.mapper.UpdateBehavior;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.UUID;

import static it.pagopa.pdv.tokenizer.connector.dao.model.Status.ACTIVE;

@Data
@FieldNameConstants(onlyExplicitlyIncluded = true)
@DynamoDbBean
public class GlobalFiscalCodeToken {

    public static final String NAMESPACE = "GLOBAL";

    private String pii;
    @FieldNameConstants.Include
    private String token = UUID.randomUUID().toString();// it should be good in the new enhanced async client
    @FieldNameConstants.Include
    private Status status = ACTIVE;


    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPii() {
        return pii;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getNamespace() {
        return NAMESPACE;
    }

    public void setNamespace(String namespace) {
        // intentionally left blank: SK is static data
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "gsi_token")
    @DynamoDbUpdateBehavior(UpdateBehavior.WRITE_IF_NOT_EXISTS)
    public String getToken() {
        return token;
    }

}
