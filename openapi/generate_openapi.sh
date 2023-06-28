#!/bin/bash

# install api-spec-converter if not present
if [ $(npm list -g | grep -c api-spec-converter) -eq 0 ]; then
  npm install -g api-spec-converter
fi

# save openapi
curl http://localhost:8080/v3/api-docs | python3 -m json.tool > ./openapi.json
jq  '."servers"[0]."url" |= "https://${host}/${basePath}"' "./openapi.json" > "./openapi.json.tpl"


# UI mode http://localhost:8080/swagger-ui/index.html
