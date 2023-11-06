package it.pagopa.pdv.tokenizer.connector.dao.handler;

import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import it.pagopa.pdv.tokenizer.connector.exception.BadRequestException;
import it.pagopa.pdv.tokenizer.connector.exception.TooManyRequestsException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
@Slf4j
@Aspect
@Component
public class ConnectorExceptionHandlingAspect {

    @AfterThrowing(pointcut = "execution(public * it.pagopa.pdv.tokenizer.connector.dao.*.*(..))", throwing = "ex")
    public void handleProvisionedThroughputExceededExceptionCall(ProvisionedThroughputExceededException ex){
        log.trace("[ConnectorExceptionHandlingAspect] handleProvisionedThroughputExceededExceptionCall");
        throw new TooManyRequestsException(ex);
    }

    @AfterThrowing(pointcut = "execution(public * it.pagopa.pdv.tokenizer.connector.dao.*.*(..))", throwing = "ex")
    public void handleAmazonDynamoDBException(AmazonDynamoDBException ex){
        log.trace("[ConnectorExceptionHandlingAspect] handleAmazonDynamoDBException");
        throw new BadRequestException(ex);
    }
}
