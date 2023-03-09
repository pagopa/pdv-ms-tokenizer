# PDV MS-Tokenizer

Spring Application implementing the **Tokenizer** microservice for the **Personal Data Vault** Project.

This microservice works in conjunction with:

- [pdv-ms-person](https://github.com/pagopa/pdv-ms-person)
- [pdv-ms-user-registry](https://github.com/pagopa/pdv-ms-user-registry)

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
- docker

### How to run unit-tests

using `junit`

```
./mvnw clean test
```

### How to run locally with Local DynamoDB 🚀

First, start a docker container with official Amazon DynamoDB image:

```
docker run -p 8000:8000 amazon/dynamodb-local
```

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
./mvnw spring-boot:run
```

---

## Mainteiners 👷🏼

See `CODEOWNERS` file
