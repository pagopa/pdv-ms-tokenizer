# PDV MS-Tokenizer

Spring Application implementing the **Tokenizer** microservice for the **Personal Data Vault** Project.

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

| **Key**         | **Value**      |
|-----------------|----------------|
| APP_SERVER_PORT | default: 8080  |
| APP_LOG_LEVEL   | default: DEBUG |

From terminal, inside the **app** package:
```
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev-local
```

### How to run locally with UAT (AWS) DynamoDB 🚀

Set the following Environment Variables:

| **Key**               | **Value**                                   |
|-----------------------|---------------------------------------------|
| APP_SERVER_PORT       | default: 8080                               |
| APP_LOG_LEVEL         | default: DEBUG                              |
| AWS_REGION            | *eu-south-1*                                |
| AWS_ACCESS_KEY_ID     | AWS Access Key ID UAT tokenizer profile     |
| AWS_SECRET_ACCESS_KEY | AWS Secret Access Key UAT tokenizer profile |
| AWS_SESSION_TOKEN     | AWS Session Token UAT tokenizer profile     |

From terminal, inside the **app** package:
```
./mvnw spring-boot:run
```

Notes: *When choosing the **port number** for this microservice, take into account that if you want to run this
with **pdv-ms-person** and **pdv-ms-user-registry**, you'll need to choose a different port for each microservice.*

---

## Mainteiners 👷🏼

See `CODEOWNERS` file