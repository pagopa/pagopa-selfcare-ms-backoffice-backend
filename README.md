# SelfCare BackOffice

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pagopa_pagopa-selfcare-ms-backoffice-backend&metric=alert_status)](https://sonarcloud.io/dashboard?id=pagopa_pagopa-selfcare-ms-backoffice-backend)
[![Integration Tests](https://github.com/pagopa/pagopa-selfcare-frontend/actions/workflows/integration_test.yml/badge.svg?branch=main)](https://github.com/pagopa/pagopa-selfcare-frontend/actions/workflows/integration_test.yml)

Microservice to manage PagoPA Backoffice

- [SelfCare BackOffice](#selfcare-backoffice)
    * [Api Documentation 📖](#api-documentation---)
    * [Technology Stack](#technology-stack)
    * [Develop Locally 💻](#develop-locally---)
        + [Prerequisites](#prerequisites-1)
        + [Run the project](#run-the-project)
        + [Spring Profiles](#spring-profiles)
        + [Testing 🧪](#testing---)
            - [Unit testing](#unit-testing)
            - [Integration testing](#integration-testing)
            - [Performance testing](#performance-testing)
    * [Contributors 👥](#contributors---)
        + [Mainteiners](#mainteiners)

---

## Api Documentation 📖

See
the [OpenApi 3 here.](https://editor.swagger.io/?url=https://raw.githubusercontent.com/pagopa/pagopa-selfcare-ms-backoffice-backend/main/openapi/openapi.json)

---

## Technology Stack

- Java 17
- Spring Boot
- Spring Web
- Hibernate
- JPA
- maven 3

---

## Develop Locally 💻

### Prerequisites

- git
- maven
- jdk-17

### Run the project

Start the springboot application with this command:

`mvn spring-boot:run -Dspring.profiles.active=local`

👀 You need to put in your local environment some variables

_An example:_

```
API_CONFIG_SERVICE_URL=https://api.dev.platform.pagopa.it
AWS_ACCESS_KEY_ID=***
AWS_REGION=eu-south-1
AWS_SECRET_ACCESS_KEY=***
AWS_SES_USER=noreply@selfcare.pagopa.it
AZURE_CLIENT_ID=***
AZURE_CLIENT_SECRET=****
AZURE_RESOURCE_GROUP=pagopa-d-api-rg
AZURE_SERVICE_NAME=pagopa-d-apim
AZURE_SID=***
AZURE_SUBSCRIPTION_ID=***
AZURE_TENANT_ID=***
ENV=local
EXTERNAL_API_SERVICE_URL=https://api.dev.selfcare.pagopa.it
JIRA_PASSWORD=***
MONGODB_CONNECTION_URI=***
PAGOPA_APIM_API_CONFIG_API_KEY_PAGOPA=***
PAGOPA_APIM_API_CONFIG_SELFCARE_INTEGRATION_API_KEY_PAGOPA=***
PAGOPA_APIM_SERVICE_URL=https://api.dev.platform.pagopa.it
PAGOPA_SELFCARE_INTEGRATION_APIM_SERVICE_URL=https://api.dev.platform.pagopa.it
PAPGOPA_APIM_GEC_API_KEY_PAGOPA=***
PAPGOPA_APIM_TAXONOMY_API_KEY_PAGOPA=***
SELFCARE_APIM_API_CONFIG_API_KEY_PAGOPA=***
SELFCARE_APIM_EXTERNAL_API_KEY_PAGOPA=***
TEST_EMAIL=***
```

### Spring Profiles

- **local**: to develop locally.
- _default (no profile set)_: The application gets the properties from the environment (for Azure).

### Testing 🧪

#### Unit testing

To run the **Junit** tests:

`mvn clean verify`

#### Integration testing

From `./integration-test/src`

1. `yarn install`
2. `yarn test`

---

## Contributors 👥

Made with ❤️ by PagoPa S.p.A.

### Mainteiners

See `CODEOWNERS` file
