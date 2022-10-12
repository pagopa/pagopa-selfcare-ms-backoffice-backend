# pagopa-selfcare-ms-backoffice-backend

## How to release

```sh
cd helm

helm dep update &&  sleep 1 &&  helm template -f values-dev.yaml .

helm upgrade -i -n selfcare -f values-dev.yaml pagopa-selfcare-ms-backoffice-backend . 
```
