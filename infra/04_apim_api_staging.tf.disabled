module "apim_api_backoffice_api_staging" {
  count = var.pr_number != null ? 1 : 0

  source = "git::https://github.com/pagopa/terraform-azurerm-v3.git//api_management_api?ref=v6.7.0"

  name                  = "${var.env_short}-${local.repo_name}"
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  product_ids           = [local.apim.product_id]
  subscription_required = false

  version_set_id = azurerm_api_management_api_version_set.api_backoffice_api.id
  api_version    = var.pr_number

  description  = local.description
  display_name = local.display_name
  path         = local.path
  protocols    = ["https"]

  service_url = null

  content_format = "openapi"
  content_value  = templatefile("../openapi/openapi.json", {
    host     = local.host
    basePath = "selfcare"
  })

  xml_content = templatefile("./policy/_base_policy.xml", {
    hostname = var.hostname
    version  = var.pr_number
    origin   = var.env_short == "d" ? "<origin>*</origin>" : "<origin>https://${local.selfcare_fe_hostname}</origin>"
  })
}
