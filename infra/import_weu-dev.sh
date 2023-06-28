#!/bin/bash
# Generated with `generate_imports.py`

# resource.azurerm_api_management_api_version_set.api_backoffice_apiConfig_api
echo 'Importing azurerm_api_management_api_version_set.api_backoffice_apiConfig_api'
./terraform.sh import dev 'azurerm_api_management_api_version_set.api_backoffice_apiConfig_api' '/subscriptions/bbe47ad4-08b3-4925-94c5-1278e5819b86/resourceGroups/pagopa-d-api-rg/providers/Microsoft.ApiManagement/service/pagopa-d-apim/apiVersionSets/d-pagopa-backoffice-apiConfig-api'


# module.apim_api_backoffice_apiConfig_api_v1
echo 'Importing module.apim_api_backoffice_apiConfig_api_v1.azurerm_api_management_api.this'
./terraform.sh import dev 'module.apim_api_backoffice_apiConfig_api_v1.azurerm_api_management_api.this' '/subscriptions/bbe47ad4-08b3-4925-94c5-1278e5819b86/resourceGroups/pagopa-d-api-rg/providers/Microsoft.ApiManagement/service/pagopa-d-apim/apis/pagopa-d-cfg-api-v1'


# module.apim_api_backoffice_apiConfig_api_v1
echo 'Importing module.apim_api_backoffice_apiConfig_api_v1.azurerm_api_management_api_policy.this[0]'
./terraform.sh import dev 'module.apim_api_backoffice_apiConfig_api_v1.azurerm_api_management_api_policy.this[0]' '/subscriptions/bbe47ad4-08b3-4925-94c5-1278e5819b86/resourceGroups/pagopa-d-api-rg/providers/Microsoft.ApiManagement/service/pagopa-d-apim/apis/pagopa-d-cfg-api-v1/policies/xml'


echo 'Import executed succesfully on dev environment! ⚡'
