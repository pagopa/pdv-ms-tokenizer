package it.pagopa.pdv.tokenizer.connector.dao.model;


import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.util.Assert;

@Data
@FieldNameConstants(onlyExplicitlyIncluded = true)
@DynamoDBTable(tableName = "Token")
public class GlobalFiscalCodeToken {

    public static final String NAMESPACE = "GLOBAL";


    @DynamoDBHashKey(attributeName = "PK")
    private String pii;

    @DynamoDBRangeKey(attributeName = "SK")
    @DynamoDBTypeConvertedEnum
    private final String namespace = NAMESPACE;

    public void setNamespace(String namespace) {
        Assert.isTrue(NAMESPACE.equals(namespace), "Only 'GLOBAL' Namespace is allowed");
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "gsi_token")
    @DynamoDBGeneratedUuid(DynamoDBAutoGenerateStrategy.CREATE)
    @FieldNameConstants.Include
    private String token;

}
