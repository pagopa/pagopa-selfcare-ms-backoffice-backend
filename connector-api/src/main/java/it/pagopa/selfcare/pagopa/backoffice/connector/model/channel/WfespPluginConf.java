package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class WfespPluginConf  extends WfespPluginConfBase{

    @JsonProperty("id_serv_plugin")
    @NotBlank
    @Size(max = 35)
    private String idServPlugin;

}
