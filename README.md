# PDV MS-Tokenizer

Spring Application implementing the **Tokenizer** microservice for the **Personal Data Vault** Project.

---

## Dependencies

Upstream:
- [pdv-ms-user-registry](https://github.com/pagopa/pdv-ms-user-registry)
- PagoPA products

Downstream:
- [Amazon DynamoDB](https://aws.amazon.com/dynamodb/?nc1=h_ls)

---

## Api Documentation 📖

See the [UAT here.](https://api.uat.tokenizer.pdv.pagopa.it/docs/tokenizeruapis/openapi.json)

See the [PROD here.](https://api.tokenizer.pdv.pagopa.it/docs/tokenizerpapis/openapi.json)


---

## Technology Stack 📚

- SDK: Java 11
- Framework: Spring Boot
- Cloud: AWS

---

## Develop Locally 💻

### Prerequisites

- git
- maven
- jdk-11

### How to run unit-tests

using `junit`

```
./mvnw clean test
```

### How to run locally with Local DynamoDB 🚀

First, download from [here](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html) the local version of DynamoDB.

Then, run the following command:
```
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
```
The above command will start a DynamoDB local version on port **8000**.
Check [here](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.UsageNotes.html) the documentation.

**N.B.** as an alternative to DynamoDBLocal, if you have a Docker engine installed, you can start a local docker container with official Amazon DynamoDB image:

```
docker run -p 8000:8000 amazon/dynamodb-local
```
Now, we need to setup the table on DynamoDB:

Launch the following test to generate the CreateTableRequest
```
./mvnw test -DfailIfNoTests=false -Dtest=DaoTestConfig#generateCreateTableRequest
```
After test result, in the directory *connector/dao/target/test/dynamodb-local-template* you'll find *Token.json* file which we can use to create the Token table on the local Dynamo instance with the following **aws cli** command:

```
aws dynamodb create-table --region local --cli-input-json file://<BASE_PATH>/Token.json
```
If you started DynamoDB local on different port from default, you have to add the following parameter from previous command: `--endpoint-url http://localhost:<PORT>`

To run the above command, you'll first need to export the following Environment Variables, with dummy values:

- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY


Then, set the following Environment Variables:

| **Key**         | **Value**                |
|-----------------|--------------------------|
| APP_SERVER_PORT | default: 8080[^app_port] |
| APP_LOG_LEVEL   | default: DEBUG           |

[^app_port]: When running multiple microservices simultaneously, a different port must be chosen for each one.

From terminal, inside the **app** package:
```
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev-local
```

### How to run locally with UAT (AWS) DynamoDB 🚀

Set the following Environment Variables:

| **Key**               | **Value**                                                      |
|-----------------------|----------------------------------------------------------------|
| APP_SERVER_PORT       | default: 8080[^app_port]                                       |
| APP_LOG_LEVEL         | default: DEBUG                                                 |
| AWS_REGION            | *eu-south-1*                                                   |
| AWS_ACCESS_KEY_ID     | AWS Access Key ID *ppa-tokenizer-data-vault-uat*[^aws_sso]     |
| AWS_SECRET_ACCESS_KEY | AWS Secret Access Key *ppa-tokenizer-data-vault-uat*[^aws_sso] |
| AWS_SESSION_TOKEN     | AWS Session Token *ppa-tokenizer-data-vault-uat*[^aws_sso]     |

[^aws_sso]: For info about AWS SSO login, see [here](https://pagopa.atlassian.net/wiki/spaces/DEVOPS/pages/466846955/AWS+-+Users+groups+and+roles#Users-and-groups---DevOps-team).

From terminal, inside the **app** package:
```
../mvnw spring-boot:run
```

---

## Mainteiners 👷🏼

See `CODEOWNERS` file
