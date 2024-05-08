package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Protocol;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class WrapperChannelDetailsDto {

    @JsonProperty("channel_code")
    @Schema(description = "Channel code")
    @NotBlank
    private String channelCode;

    @JsonProperty("broker_description")
    @Schema(description = "Broker description. Read only field")
    @NotBlank
    private String brokerDescription;


    @JsonProperty("broker_psp_code")
    @Schema(description = " psp code")
    @NotBlank
    private String brokerPspCode;


    @JsonProperty("target_host")
    @Schema(description = " target host")
    @NotBlank
    private String targetHost;

    @JsonProperty("target_port")
    @Schema(description = " target port")
    @NotNull
    private Long targetPort;

    @JsonProperty("target_path")
    @Schema(description = " target path's")
    @NotBlank
    private String targetPath;


    // CANALI_NODO

    @JsonProperty("redirect_ip")
    @Schema(description = " redirect ip")
    private String redirectIp;

    @JsonProperty("redirect_path")
    @Schema(description = " redirect path")
    private String redirectPath;

    @JsonProperty("redirect_port")
    @Schema(description = " redirect port")
    private Long redirectPort;

    @JsonProperty("redirect_query_string")
    @Schema(description = " redirect query string")
    private String redirectQueryString;

    @JsonProperty("redirect_protocol")
    @Schema(description = " redirect protocol")
    @NotNull
    private Protocol redirectProtocol;

    @JsonProperty("payment_types")
    @Schema(description = " List of payment types")
    @NotNull
    private List<String> paymentTypeList;

    @Schema(description = "channel note description by operation team")
    private String note = "";

    @Schema(description = "channel's validation status")
    private WrapperStatus status = WrapperStatus.TO_CHECK;

    @JsonProperty(required = true)
    @NotBlank
    @Schema(description = "Url jira for ChannelDetail validation")
    private String validationUrl;
}
