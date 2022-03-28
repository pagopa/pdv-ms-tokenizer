package it.pagopa.pdv.tokenizer.connector.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTableMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import it.pagopa.pdv.tokenizer.connector.TokenizerConnector;
import it.pagopa.pdv.tokenizer.connector.dao.model.GlobalFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.dao.model.NamespacedFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.model.Namespace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Map;

@Slf4j
@Service
class TokenizerConnectorImpl implements TokenizerConnector {

    public static final String TABLE_NAME = "Token";

    private final DynamoDBMapper dynamoDBMapper;
    private final DynamoDB dynamoDB;
    private final Table table;
    private final DynamoDBTableMapper<NamespacedFiscalCodeToken, String, Namespace> namespacedFiscalCodeDynamoDBTableMapper;
    private final DynamoDBTableMapper<GlobalFiscalCodeToken, String, Namespace> globalFiscalCodeDynamoDBTableMapper;


    TokenizerConnectorImpl(DynamoDBMapper dynamoDBMapper, DynamoDB dynamoDB) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.dynamoDB = dynamoDB;
        table = dynamoDB.getTable(TABLE_NAME);
        namespacedFiscalCodeDynamoDBTableMapper = dynamoDBMapper.newTableMapper(NamespacedFiscalCodeToken.class);
        globalFiscalCodeDynamoDBTableMapper = dynamoDBMapper.newTableMapper(GlobalFiscalCodeToken.class);
    }


    @Override
    public String save(String pii, Namespace namespace) {//FIXME: get namespace from "logged user info/request info"
        Assert.hasText(pii, "A Private Data is required");
        Assert.notNull(namespace, "A Namespace is required");
        String globalToken;
        GlobalFiscalCodeToken globalFiscalCodeToken = new GlobalFiscalCodeToken();
        globalFiscalCodeToken.setPii(pii);
        try {
            globalFiscalCodeDynamoDBTableMapper.saveIfNotExists(globalFiscalCodeToken);
            globalToken = globalFiscalCodeToken.getToken();
        } catch (ConditionalCheckFailedException e) {
            Item item = table.getItem(globalFiscalCodeDynamoDBTableMapper.hashKey().name(),
                    globalFiscalCodeToken.getPii(),
                    globalFiscalCodeDynamoDBTableMapper.rangeKey().name(),
                    Namespace.GLOBAL.toString(),
                    "#field",
                    Map.of("#field", "token"));
            globalToken = item.getString("token");
        }

        String token;
        NamespacedFiscalCodeToken namespacedFiscalCodeToken = new NamespacedFiscalCodeToken();
        namespacedFiscalCodeToken.setPii(pii);
        namespacedFiscalCodeToken.setNamespace(namespace);
        namespacedFiscalCodeToken.setGlobalToken(globalToken);
        try {
            namespacedFiscalCodeDynamoDBTableMapper.saveIfNotExists(namespacedFiscalCodeToken);
            token = namespacedFiscalCodeToken.getToken();
        } catch (ConditionalCheckFailedException e) {

            Item item = table.getItem(namespacedFiscalCodeDynamoDBTableMapper.hashKey().name(),
                    namespacedFiscalCodeToken.getPii(),
                    namespacedFiscalCodeDynamoDBTableMapper.rangeKey().name(),
                    namespacedFiscalCodeToken.getNamespace().toString(),
                    "#field",
                    Map.of("#field", "token"));
            token = item.getString("token");
        }
        return token;
    }


    @Override
    public String findById(String pii, Namespace namespace) {
        Assert.hasText(pii, "A Private Data is required");
        Assert.notNull(namespace, "A Namespace is required");
        Item item = table.getItem(namespacedFiscalCodeDynamoDBTableMapper.hashKey().name(),
                "CF#" + pii,
                namespacedFiscalCodeDynamoDBTableMapper.rangeKey().name(),
                namespace.toString(),
                "#field",
                Map.of("#field", "token"));
        return item.getString("token");
    }


    @Override
    public String findPiiByToken(String token) {
        Assert.hasText(token, "A token is required");
        String pii;
        Index index = table.getIndex("gsi_token");
        ItemCollection<QueryOutcome> itemCollection = index.query(new QuerySpec()
                .withHashKey("token", token)
                .withProjectionExpression("pii"));
        Iterator<Page<Item, QueryOutcome>> iterator = itemCollection.pages().iterator();
        if (iterator.hasNext()) {
            Page<Item, QueryOutcome> page = iterator.next();
            if (page.getLowLevelResult().getItems().size() == 0) {
                throw new RuntimeException("Not Found");//FIXME: change exception
            } else if (page.getLowLevelResult().getItems().size() > 1) {
                throw new RuntimeException("Too many results");//FIXME: change exception
            } else {
                pii = page.getLowLevelResult().getItems().get(0).getString("pii");
            }
        } else {
            throw new RuntimeException("Not Found");//FIXME: change exception
        }
        return pii;
    }

}
