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
public class NamespacedFiscalCodeToken {

    private String pii;
    private String namespace;
    @FieldNameConstants.Include
    private String token;
    @FieldNameConstants.Include
    private String globalToken;
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
        return namespace;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "gsi_token")
    @DynamoDbUpdateBehavior(UpdateBehavior.WRITE_IF_NOT_EXISTS)
    public String getToken() {
        return token;
    }

    public NamespacedFiscalCodeToken() {
        token = UUID.randomUUID().toString();
    }

}
