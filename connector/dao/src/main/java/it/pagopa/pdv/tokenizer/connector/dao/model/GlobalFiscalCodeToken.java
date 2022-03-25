package it.pagopa.pdv.tokenizer.connector.dao.model;


import com.amazonaws.services.dynamodbv2.datamodeling.*;
import it.pagopa.pdv.tokenizer.connector.model.Namespace;
import lombok.Data;
import org.springframework.util.Assert;

@Data
@DynamoDBTable(tableName = "PiiData")
public class GlobalFiscalCodeToken {

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
    private final Namespace namespace = Namespace.GLOBAL;

    public void setNamespace(Namespace namespace) {
        Assert.isTrue(Namespace.GLOBAL.equals(namespace), "Only 'GLOBAL' Namespace is allowed");
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "gsi_token")
    @DynamoDBGeneratedUuid(DynamoDBAutoGenerateStrategy.CREATE)
    private String token;

}
