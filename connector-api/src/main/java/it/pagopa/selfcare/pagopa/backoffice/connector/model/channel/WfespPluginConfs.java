package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class WfespPluginConfs {

    @JsonProperty("wfesp_plugin_confs")
    @NotNull
    @Valid
    private List<WfespPluginConf> wfespPluginConfList;
}
