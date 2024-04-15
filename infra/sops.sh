#!/bin/bash

#set -x

action=$1
env=$2

azurekvurl=`az keyvault key show --name backoffice-sops-key --vault-name pagopa-${env:0:1}-selfcare-kv --query key.kid | sed 's/"//g'`

if [ "$action" == "e" ]; then
  sops --encrypt --azure-kv $azurekvurl --input-type dotenv --output-type  dotenv ./env/$env/decrypted.env > ./env/$env/encrypted.env
fi;

if [ "$action" == "d" ]; then
  sops --decrypt --azure-kv $azurekvurl --input-type dotenv --output-type dotenv ./env/$env/encrypted.env > ./env/$env/decrypted.env
fi;


echo 'done'
