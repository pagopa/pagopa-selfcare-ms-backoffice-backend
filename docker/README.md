# Docker Environment
`run_docker.sh` is a script to launch the image of this microservice and all the dependencies on Docker.

## How to use
You can use local, dev, uat or prod images

`sh ./run_docker.sh <local|dev|uat|prod>`

This command will generate an .env file for you taking configuration parameter from specified environment values file.
Secrets are read from azure corresponding kv, allowing to run the service locally against a specific environment.

Running with local env, instead, will use the `./docker/.env.local` file to startup project with all mocked dependencies 

docker compose will contain the following containers:

| container name | listening port | description                                                                 | target env |
|----------------|----------------|-----------------------------------------------------------------------------|------------|
| selfcare       | 8080           | this service                                                                | all        |
| mongo          | 27017          | Mongo db instance, initialized with `./docker/mongodb/mongo-init.js` script | local      |
| mongo-express  | 8081           | [web based mongo db client](https://github.com/mongo-express/mongo-express) | local      |

target env column specify which container is run for which profile: for example, using dev environment will not start mongo and mongo-express
containers since the dev CosmosDB will be used instead

_Note_: if you run the script without the parameter, `local` is used as default.

