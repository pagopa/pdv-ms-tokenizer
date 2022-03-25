package it.pagopa.pdv.tokenizer.connector.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import it.pagopa.pdv.tokenizer.connector.PersonConnector;
import it.pagopa.pdv.tokenizer.connector.dao.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class PersonConnectorImpl implements PersonConnector {

    private final DynamoDBMapper dynamoDBMapper;
    private final DynamoDB dynamoDB;

    public PersonConnectorImpl(DynamoDBMapper dynamoDBMapper, DynamoDB dynamoDB) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.dynamoDB = dynamoDB;
    }


    public Optional<Person> findById(String id) {
        return Optional.ofNullable(dynamoDBMapper.load(Person.class, id));
    }


    public Optional<Person> findByFiscalCode(String fiscalCode) {
        DynamoDBQueryExpression<Person> queryExpression = new DynamoDBQueryExpression<Person>()
                .withIndexName("gsi_fiscal_code")
                .withConsistentRead(false)
                .withKeyConditionExpression("fiscalCode = :v1")
                .withExpressionAttributeValues(Map.of(":v1", new AttributeValue().withS(fiscalCode)));
        PaginatedQueryList<Person> queryResult = dynamoDBMapper.query(Person.class, queryExpression);
        Optional<Person> result;
        if (queryResult.isEmpty()) {
            result = Optional.empty();
        } else if (queryResult.size() == 1) {
            result = Optional.of(queryResult.get(0));
        } else {
            throw new IllegalStateException();//TODO insert message
        }
        return result;
    }


}
