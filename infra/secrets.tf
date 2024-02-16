data "azurerm_key_vault" "kv" {
  name                = "${local.product}-${local.domain}-kv"
  resource_group_name = "${local.product}-${local.domain}-sec-rg"
}


resource "azurerm_key_vault_key" "sops_key" {
  name         = "${local.service}-sops-key"
  key_vault_id = data.azurerm_key_vault.kv.id
  key_type     = "RSA"
  key_size     = 2048

  key_opts = [
    "decrypt",
    "encrypt",
  ]
}
