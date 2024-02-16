#!/bin/bash

#set -x

action=$1

azurekvurl=`az keyvault key show --name backoffice-sops-key --vault-name pagopa-d-selfcare-kv --query key.kid | sed 's/"//g'`

if [ "$action" == "e" ]; then
  sops --encrypt --azure-kv $azurekvurl --input-type dotenv --output-type  dotenv ./decrypted_env > ./encrypted_env
fi;

if [ "$action" == "d" ]; then
  sops --decrypt --azure-kv $azurekvurl --input-type dotenv --output-type dotenv ./encrypted_env > ./decrypted_env
fi;


echo 'done'
