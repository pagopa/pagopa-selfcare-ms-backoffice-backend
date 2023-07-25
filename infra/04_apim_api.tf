locals {
  repo_name = "pagopa-selfcare-ms-backoffice-backend"

  display_name = "PagoPA backoffice ApiConfig for auth"
  description  = "API to manage PSP and EC configurations"
  path  = "backoffice/apiconfig/auth"

  host         = "api.${var.apim_dns_zone_prefix}.${var.external_domain}"
  hostname     = var.hostname

  selfcare_fe_hostname = var.env == "prod" ? "selfcare.platform.pagopa.it" : "selfcare.${var.env}.platform.pagopa.it"

}

resource "azurerm_api_management_api_version_set" "api_backoffice_apiConfig_api" {
  name                = format("%s-${local.repo_name}", var.env_short)
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = local.display_name
  versioning_scheme   = "Segment"
}

module "apim_api_backoffice_apiConfig_api_v1" {
  source = "git::https://github.com/pagopa/terraform-azurerm-v3.git//api_management_api?ref=v6.7.0"

  name                  = format("%s-${local.repo_name}", var.env_short)
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  product_ids           = [local.apim.product_id]
  subscription_required = false

  version_set_id = azurerm_api_management_api_version_set.api_backoffice_apiConfig_api.id
  api_version    = "v1"

  description  = local.description
  display_name = local.display_name
  path         = local.path
  protocols    = ["https"]

  service_url = null

  content_format = "openapi"
  content_value  = templatefile("../openapi/openapi.json", {
    host = local.host
    basePath = "selfcare"
  })

  xml_content = templatefile("./policy/_base_policy.xml", {
    hostname = var.hostname
    origin       = local.selfcare_fe_hostname
    local_origin = var.env_short == "d" ? "<origin>https://localhost:3000</origin>" : ""
  })
}

