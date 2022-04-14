package it.pagopa.pdv.tokenizer.connector.dao.model;


import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants(onlyExplicitlyIncluded = true)
@DynamoDBTable(tableName = "Token")
public class GlobalFiscalCodeToken {

    public static final String NAMESPACE = "GLOBAL";


    @DynamoDBHashKey(attributeName = "PK")
    private String pii;

    @DynamoDBRangeKey(attributeName = "SK")
    public String getNamespace() {
        return NAMESPACE;
    }

    public void setNamespace(String namespace) {
        // intentionally left blank: SK is static data
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "gsi_token")
    @DynamoDBGeneratedUuid(DynamoDBAutoGenerateStrategy.CREATE)
    @FieldNameConstants.Include
    private String token;

}
