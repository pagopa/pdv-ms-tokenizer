package it.pagopa.pdv.tokenizer.connector.dao.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import static it.pagopa.pdv.tokenizer.connector.dao.model.Status.ACTIVE;

@Data
@FieldNameConstants(onlyExplicitlyIncluded = true)
@DynamoDBTable(tableName = "Token")
public class NamespacedFiscalCodeToken {

    @DynamoDBHashKey(attributeName = "PK")
    private String pii;

    @DynamoDBRangeKey(attributeName = "SK")
    private String namespace;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "gsi_token")
    @DynamoDBGeneratedUuid(DynamoDBAutoGenerateStrategy.CREATE)
    @FieldNameConstants.Include
    private String token;

    @DynamoDBAttribute
    @FieldNameConstants.Include
    private String globalToken;

    @DynamoDBAttribute
    @FieldNameConstants.Include
    @DynamoDBTypeConvertedEnum
    private Status status = ACTIVE;

}
