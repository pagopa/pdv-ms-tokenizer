package it.pagopa.pdv.tokenizer.connector.dao.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "namespacedPeople")
public class NamespacedPerson {

    @DynamoDBHashKey
    private String privateData;

    @DynamoDBRangeKey
    private String namespace;

    @DynamoDBGeneratedUuid(DynamoDBAutoGenerateStrategy.CREATE)
    private String uuid;

}
