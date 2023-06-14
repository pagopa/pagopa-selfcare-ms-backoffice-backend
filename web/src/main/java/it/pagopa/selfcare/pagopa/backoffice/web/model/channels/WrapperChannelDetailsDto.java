package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Protocol;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class WrapperChannelDetailsDto {

    @JsonProperty("channel_code")
    @ApiModelProperty(value = "${swagger.model.channel.code}")
    @NotBlank
    private String channelCode;//ok

    @JsonProperty("broker_description")
    @ApiModelProperty(value = "${swagger.model.broker.description}")
    @NotBlank
    private String brokerDescription;//ok


    @JsonProperty("broker_psp_code")
    @ApiModelProperty(value = "${swagger.model.channel.details.brokerPspCode}")
    @NotBlank
    private String brokerPspCode;//ok


    @JsonProperty("target_host")
    @ApiModelProperty(value = "${swagger.model.channel.details.targetHost}")
    @NotBlank
    private String targetHost;//ok

    @JsonProperty("target_port")
    @ApiModelProperty(value = "${swagger.model.channel.details.targetPort}")
    @NotNull
    private Long targetPort;//ok

    @JsonProperty("target_path")
    @ApiModelProperty(value = "${swagger.model.channel.details.targetPath}")
    @NotBlank
    private String targetPath;//ok


    // CANALI_NODO

    @JsonProperty("redirect_ip")
    @ApiModelProperty(value = "${swagger.model.channel.details.redirectIp}")
    @NotBlank
    private String redirectIp;//ok

    @JsonProperty("redirect_path")
    @ApiModelProperty(value = "${swagger.model.channel.details.redirectPath}")
    @NotBlank
    private String redirectPath;//ok

    @JsonProperty("redirect_port")
    @ApiModelProperty(value = "${swagger.model.channel.details.redirectPort}")
    @NotNull
    private Long redirectPort;//ok

    @JsonProperty("redirect_query_string")
    @ApiModelProperty(value = "${swagger.model.channel.details.redirectQueryString}")
    @NotBlank
    private String redirectQueryString;//ok

    @JsonProperty("redirect_protocol")
    @ApiModelProperty(value = "${swagger.model.channel.details.redirectProtocol}")
    @NotNull
    private Protocol redirectProtocol;//ok

    @JsonProperty("payment_types")
    @ApiModelProperty(value = "${swagger.model.PspChannelPaymentTypesResource.list}")
    @NotNull
    private List<String> paymentTypeList;//ok

    @ApiModelProperty(value = "${swagger.model.channel.details.note}")
    private String note = "";

    @ApiModelProperty(value = "${swagger.model.channel.details.status}")
    private WrapperStatus status = WrapperStatus.TO_CHECK;

    @JsonProperty(required = true)
    @NotBlank
    @ApiModelProperty(value = "${swagger.model.channel.details.validationUrl}")
    private String validationUrl;
}