package it.pagopa.pdv.tokenizer.connector.dao.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants(onlyExplicitlyIncluded = true)
@DynamoDBTable(tableName = "Token")
public class NamespacedFiscalCodeToken {

    @DynamoDBHashKey(attributeName = "PK")
    private String pii;

    @DynamoDBRangeKey(attributeName = "SK")
    @DynamoDBTypeConvertedEnum
    private String namespace;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "gsi_token")
    @DynamoDBGeneratedUuid(DynamoDBAutoGenerateStrategy.CREATE)
    @FieldNameConstants.Include
    private String token;

    @DynamoDBAttribute
    private String globalToken;

}
