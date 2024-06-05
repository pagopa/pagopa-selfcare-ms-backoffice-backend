locals {
  product = "${var.prefix}-${var.env_short}"
  domain  = "selfcare"
  service = "backoffice"
  apim    = {
    name       = "${local.product}-apim"
    rg         = "${local.product}-api-rg"
    product_id = "selfcare-be"
  }
  apim_weu_core    = {
    name       = "${var.prefix}-${var.env_short}-weu-core-apim-v2"
    rg         = "${local.product}-api-rg"
    product_id = "selfcare-be"
  }
}

