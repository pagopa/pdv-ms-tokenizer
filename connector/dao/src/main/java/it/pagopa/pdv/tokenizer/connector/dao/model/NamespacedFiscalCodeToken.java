package it.pagopa.pdv.tokenizer.connector.dao.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import it.pagopa.pdv.tokenizer.connector.model.Namespace;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "Token")
public class NamespacedFiscalCodeToken {

    @DynamoDBHashKey(attributeName = "PK")
    private String pii;

    @DynamoDBRangeKey(attributeName = "SK")
    @DynamoDBTypeConvertedEnum
    private Namespace namespace;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "gsi_token")
    @DynamoDBGeneratedUuid(DynamoDBAutoGenerateStrategy.CREATE)
    private String token;

    @DynamoDBAttribute
    private String globalToken;

}
