package it.pagopa.pdv.tokenizer.connector.dao.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import it.pagopa.pdv.tokenizer.connector.model.Namespace;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "PiiData")
public class NamespacedFiscalCodeToken {

    @DynamoDBAttribute
    private String pii;

    @DynamoDBHashKey(attributeName = "PK")
    public String getPK() {
        return "CF#" + pii;
    }

    public void setPK(String pk) {
        // intentionally left blank: PK is set by setting pii attribute
    }

    @DynamoDBRangeKey
    @DynamoDBTypeConvertedEnum
    private Namespace namespace;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "gsi_token")
    @DynamoDBGeneratedUuid(DynamoDBAutoGenerateStrategy.CREATE)
    private String token;

    @DynamoDBAttribute
    private String globalToken;

}
