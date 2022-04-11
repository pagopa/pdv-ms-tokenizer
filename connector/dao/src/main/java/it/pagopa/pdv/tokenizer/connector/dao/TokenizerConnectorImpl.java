package it.pagopa.pdv.tokenizer.connector.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperTableModel;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTableMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import it.pagopa.pdv.tokenizer.connector.TokenizerConnector;
import it.pagopa.pdv.tokenizer.connector.dao.model.GlobalFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.dao.model.NamespacedFiscalCodeToken;
import it.pagopa.pdv.tokenizer.connector.model.TokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Map;

@Slf4j
@Service
public class TokenizerConnectorImpl implements TokenizerConnector {

    public static final String TABLE_NAME = "Token";

    private final DynamoDBMapper dynamoDBMapper;
    private final DynamoDB dynamoDB;
    private final Table table;
    private final DynamoDBTableMapper<NamespacedFiscalCodeToken, String, String> namespacedFiscalCodeDynamoDBTableMapper;
    private final DynamoDBTableMapper<GlobalFiscalCodeToken, String, String> globalFiscalCodeDynamoDBTableMapper;
    private final DynamoDBMapperTableModel<NamespacedFiscalCodeToken> namespacedFiscalCodeTableModel;


    TokenizerConnectorImpl(DynamoDBMapper dynamoDBMapper, DynamoDB dynamoDB) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.dynamoDB = dynamoDB;
        table = dynamoDB.getTable(TABLE_NAME);
        namespacedFiscalCodeDynamoDBTableMapper = dynamoDBMapper.newTableMapper(NamespacedFiscalCodeToken.class);
        globalFiscalCodeDynamoDBTableMapper = dynamoDBMapper.newTableMapper(GlobalFiscalCodeToken.class);
        namespacedFiscalCodeTableModel = dynamoDBMapper.getTableModel(NamespacedFiscalCodeToken.class);
    }


    @Override
    public TokenDto save(String pii, String namespace) {
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        TokenDto tokenDto = new TokenDto();
        GlobalFiscalCodeToken globalFiscalCodeToken = new GlobalFiscalCodeToken();
        globalFiscalCodeToken.setPii(pii);
        try {
            globalFiscalCodeDynamoDBTableMapper.saveIfNotExists(globalFiscalCodeToken);
            tokenDto.setRootToken(globalFiscalCodeToken.getToken());
        } catch (ConditionalCheckFailedException e) {
            Item item = table.getItem(globalFiscalCodeDynamoDBTableMapper.hashKey().name(),
                    globalFiscalCodeToken.getPii(),
                    globalFiscalCodeDynamoDBTableMapper.rangeKey().name(),
                    GlobalFiscalCodeToken.NAMESPACE,
                    "#0",
                    Map.of("#0", GlobalFiscalCodeToken.Fields.token));
            tokenDto.setRootToken(item.getString(GlobalFiscalCodeToken.Fields.token));
        }

        NamespacedFiscalCodeToken namespacedFiscalCodeToken = new NamespacedFiscalCodeToken();
        namespacedFiscalCodeToken.setPii(pii);
        namespacedFiscalCodeToken.setNamespace(namespace);
        namespacedFiscalCodeToken.setGlobalToken(tokenDto.getRootToken());
        try {
            namespacedFiscalCodeDynamoDBTableMapper.saveIfNotExists(namespacedFiscalCodeToken);
            tokenDto.setToken(namespacedFiscalCodeToken.getToken());
        } catch (ConditionalCheckFailedException e) {
            Item item = table.getItem(namespacedFiscalCodeTableModel.hashKey().name(),
                    namespacedFiscalCodeToken.getPii(),
                    namespacedFiscalCodeTableModel.rangeKey().name(),
                    namespacedFiscalCodeToken.getNamespace().toString(),
                    "#0",
                    Map.of("#0", NamespacedFiscalCodeToken.Fields.token));
            tokenDto.setToken(item.getString(NamespacedFiscalCodeToken.Fields.token));
        }
        return tokenDto;
    }


    @Override
    public String findById(String pii, String namespace) {
        Assert.hasText(pii, "A Private Data is required");
        Assert.hasText(namespace, "A Namespace is required");
        NamespacedFiscalCodeToken primaryKey = namespacedFiscalCodeTableModel.createKey(pii, namespace);
        Item item = table.getItem(namespacedFiscalCodeTableModel.hashKey().name(),
                primaryKey.getPii(),
                namespacedFiscalCodeTableModel.rangeKey().name(),
                primaryKey.getNamespace(),
                "#0",
                Map.of("#0", NamespacedFiscalCodeToken.Fields.token));
        return item.getString(NamespacedFiscalCodeToken.Fields.token);
    }


    @Override
    public String findPiiByToken(String token) {
        Assert.hasText(token, "A token is required");
        String pii;
        Index index = table.getIndex("gsi_token");
        ItemCollection<QueryOutcome> itemCollection = index.query(new QuerySpec()
                .withHashKey(NamespacedFiscalCodeToken.Fields.token, token)
                .withProjectionExpression(namespacedFiscalCodeTableModel.hashKey().name()));
        Iterator<Page<Item, QueryOutcome>> iterator = itemCollection.pages().iterator();
        if (iterator.hasNext()) {
            Page<Item, QueryOutcome> page = iterator.next();
            if (page.getLowLevelResult().getItems().size() == 0) {
                throw new RuntimeException("Not Found");//FIXME: change exception
            } else if (page.getLowLevelResult().getItems().size() > 1) {
                throw new RuntimeException("Too many results");//FIXME: change exception
            } else {
                pii = page.getLowLevelResult().getItems().get(0).getString(namespacedFiscalCodeTableModel.hashKey().name());
            }
        } else {
            throw new RuntimeException("Not Found");//FIXME: change exception
        }
        return pii;
    }

}
