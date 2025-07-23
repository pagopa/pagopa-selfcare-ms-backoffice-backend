# SelfCare BackOffice

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pagopa_pagopa-selfcare-ms-backoffice-backend&metric=alert_status)](https://sonarcloud.io/dashboard?id=pagopa_pagopa-selfcare-ms-backoffice-backend)
[![Integration Tests](https://github.com/pagopa/pagopa-selfcare-frontend/actions/workflows/ci_integration_test.yml/badge.svg?branch=main)](https://github.com/pagopa/pagopa-selfcare-frontend/actions/workflows/ci_integration_test.yml)

Microservice to manage PagoPA Backoffice

- [SelfCare BackOffice](#selfcare-backoffice)
    * [Api Documentation 📖](#api-documentation-)
    * [Technology Stack](#technology-stack)
    * [Develop Locally 💻](#develop-locally-)
        + [Prerequisites](#prerequisites)
        + [Run the project](#run-the-project)
        + [Spring Profiles](#spring-profiles)
        + [Testing 🧪](#testing-)
            - [Unit testing](#unit-testing)
            - [Integration testing](#integration-testing)
    * [Contributors 👥](#contributors-)
        + [Maintainers](#maintainers)

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

#### Local Environment

👀 You need to put in your local environment some variables

An example local environment file could be found at `./docker/.env.local`

To build & startup project locally use the following command:

## myLib documentation
see documentation [here](docker/README.md)

If you have access to azure dev, you can use `./infra/sops.sh` script to decrypt `./infra/env/dev/encrypted_env` to
create a valid env file.
_You need to have sops installed._

Running docker with local target will generate/overwrite a local .env file cloning the `./docker/.env.local` file

```
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

### Maintainers

See `CODEOWNERS` file
