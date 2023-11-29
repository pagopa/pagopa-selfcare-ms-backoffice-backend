package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Protocol;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class WrapperChannelDetailsDto {

    @JsonProperty("channel_code")
    @ApiModelProperty(value = "Channel code")
    @NotBlank
    private String channelCode;//ok

    @JsonProperty("broker_description")
    @ApiModelProperty(value = "Broker description. Read only field")
    @NotBlank
    private String brokerDescription;//ok


    @JsonProperty("broker_psp_code")
    @ApiModelProperty(value = " psp code")
    @NotBlank
    private String brokerPspCode;//ok


    @JsonProperty("target_host")
    @ApiModelProperty(value = " target host")
    @NotBlank
    private String targetHost;//ok

    @JsonProperty("target_port")
    @ApiModelProperty(value = " target port")
    @NotNull
    private Long targetPort;//ok

    @JsonProperty("target_path")
    @ApiModelProperty(value = " target path's")
    @NotBlank
    private String targetPath;//ok


    // CANALI_NODO

    @JsonProperty("redirect_ip")
    @ApiModelProperty(value = " redirect ip")
    private String redirectIp;//ok

    @JsonProperty("redirect_path")
    @ApiModelProperty(value = " redirect path")
    private String redirectPath;//ok

    @JsonProperty("redirect_port")
    @ApiModelProperty(value = " redirect port")
    private Long redirectPort;//ok

    @JsonProperty("redirect_query_string")
    @ApiModelProperty(value = " redirect query string")
    private String redirectQueryString;//ok

    @JsonProperty("redirect_protocol")
    @ApiModelProperty(value = " redirect protocol")
    @NotNull
    private Protocol redirectProtocol;//ok

    @JsonProperty("payment_types")
    @ApiModelProperty(value = " List of payment types")
    @NotNull
    private List<String> paymentTypeList;//ok

    @ApiModelProperty(value = "channel note description by operation team")
    private String note = "";

    @ApiModelProperty(value = "channel's validation status")
    private WrapperStatus status = WrapperStatus.TO_CHECK;

    @JsonProperty(required = true)
    @NotBlank
    @ApiModelProperty(value = "Url jira for ChannelDetail validation")
    private String validationUrl;
}
