locals {
  product = "${var.prefix}-${var.env_short}"
  domain  = "selfcare"
  service = "backoffice"
  apim    = {
    name       = "${local.product}-apim"
    rg         = "${local.product}-api-rg"
    product_id = "selfcare-be"
  }
}

