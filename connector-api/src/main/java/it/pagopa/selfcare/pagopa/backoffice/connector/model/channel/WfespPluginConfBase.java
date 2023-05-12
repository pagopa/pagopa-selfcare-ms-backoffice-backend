package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class WfespPluginConfBase {

    @JsonProperty("pag_const_string_profile")
    @Size(max = 150)
    private String profiloPagConstString;

    @JsonProperty("pag_soap_rule_profile")
    @Size(max = 150)
    private String profiloPagSoapRule;

    @JsonProperty("pag_rpt_xpath_profile")
    @Size(max = 150)
    private String profiloPagRptXpath;

    @JsonProperty("id_bean")
    @Size(max = 255)
    private String idBean;
}
