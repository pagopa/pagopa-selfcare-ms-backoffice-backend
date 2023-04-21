package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PaymentModel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Protocol;
import lombok.Data;
import lombok.ToString;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChannelDetailsResource extends ChannelResource {

    @JsonProperty("password")
    @ApiModelProperty(value = "${swagger.model.channel.details.password}")
    private String password;

    @JsonProperty("new_password")
    @ApiModelProperty(value = "${swagger.model.channel.details.newPassword}")
    private String newPassword;

    @JsonProperty("protocol")
    @ApiModelProperty(value = "${swagger.model.channel.details.protocol}")
    @NotNull
    private Protocol protocol;

    @JsonProperty("ip")
    @ApiModelProperty(value = "${swagger.model.channel.details.ip}")
    private String ip;

    @JsonProperty("port")
    @ApiModelProperty(value = "${swagger.model.channel.details.port}")
    @NotNull
    private Long port;

    @JsonProperty("service")
    @ApiModelProperty(value = "${swagger.model.channel.details.service}")
    private String service;

    @JsonProperty("broker_psp_code")
    @ApiModelProperty(value = "${swagger.model.channel.details.brokerPspCode}")
    @NotBlank
    private String brokerPspCode;

    @JsonProperty("proxy_enabled")
    @ApiModelProperty(value = "${swagger.model.channel.details.proxyEnabled}")
    private Boolean proxyEnabled;

    @JsonProperty("proxy_host")
    @ApiModelProperty(value = "${swagger.model.channel.details.proxyHost}")
    private String proxyHost;


    @JsonProperty("proxy_port")
    @ApiModelProperty(value = "${swagger.model.channel.details.proxyPort}")
    private Long proxyPort;

    @JsonProperty("proxy_username")
    @ApiModelProperty(value = "${swagger.model.channel.details.proxyUsername}")
    private String proxyUsername;

    @ToString.Exclude
    @JsonProperty("proxy_password")
    @ApiModelProperty(value = "${swagger.model.channel.details.proxyPassword}")
    private String proxyPassword;

    @JsonProperty("target_host")
    @ApiModelProperty(value = "${swagger.model.channel.details.targetHost}")
    private String targetHost;

    @JsonProperty("target_port")
    @ApiModelProperty(value = "${swagger.model.channel.details.targetPort}")
    private Long targetPort;

    @JsonProperty("target_path")
    @ApiModelProperty(value = "${swagger.model.channel.details.targetPath}")
    private String targetPath;

    @JsonProperty("thread_number")
    @ApiModelProperty(value = "${swagger.model.channel.details.threadNumber}")
    @NotNull
    private Long threadNumber;

    @JsonProperty("timeout_a")
    @ApiModelProperty(value = "${swagger.model.channel.details.timeoutA}")
    @NotNull
    private Long timeoutA;

    @JsonProperty("timeout_b")
    @ApiModelProperty(value = "${swagger.model.channel.details.timeoutB}")
    @NotNull
    private Long timeoutB;

    @JsonProperty("timeout_c")
    @ApiModelProperty(value = "${swagger.model.channel.details.timeoutC}")
    @NotNull
    private Long timeoutC;

    @JsonProperty("npm_service")
    @ApiModelProperty(value = "${swagger.model.channel.details.npmService}")
    private String npmService;

    @JsonProperty("new_fault_code")
    @ApiModelProperty(value = "${swagger.model.channel.details.newFaultCode}")
    private Boolean newFaultCode;

    // CANALI_NODO

    @JsonProperty("redirect_ip")
    @ApiModelProperty(value = "${swagger.model.channel.details.redirectIp}")
    private String redirectIp;

    @JsonProperty("redirect_path")
    @ApiModelProperty(value = "${swagger.model.channel.details.redirectPath}")
    private String redirectPath;


    @JsonProperty("redirect_port")
    @ApiModelProperty(value = "${swagger.model.channel.details.redirectPort}")
    private Long redirectPort;

    @JsonProperty("redirect_query_string")
    @ApiModelProperty(value = "${swagger.model.channel.details.redirectQueryString}")
    private String redirectQueryString;

    @JsonProperty("redirect_protocol")
    @ApiModelProperty(value = "${swagger.model.channel.details.redirectProtocol}")
    private Protocol redirectProtocol;

    @JsonProperty("payment_model")
    @ApiModelProperty(value = "${swagger.model.channel.details.paymentModel}")
    @NotNull
    private PaymentModel paymentModel;

    @JsonProperty("serv_plugin")
    @ApiModelProperty(value = "${swagger.model.channel.details.servPlugin}")
    private String servPlugin;

    @JsonProperty("rt_push")
    @ApiModelProperty(value = "${swagger.model.channel.details.rtPush}")
    @NotNull
    private Boolean rtPush;

    @JsonProperty("on_us")
    @ApiModelProperty(value = "${swagger.model.channel.details.onUs}")
    @NotNull
    private Boolean onUs;

    @JsonProperty("card_chart")
    @ApiModelProperty(value = "${swagger.model.channel.details.cardChart}")
    @NotNull
    private Boolean cardChart;

    @JsonProperty("recovery")
    @ApiModelProperty(value = "${swagger.model.channel.details.recovery}")
    @NotNull
    private Boolean recovery;

    @JsonProperty("digital_stamp_brand")
    @ApiModelProperty(value = "${swagger.model.channel.details.digitalStampBrand}")
    @NotNull
    private Boolean digitalStampBrand;

    @JsonProperty("flag_io")
    @ApiModelProperty(value = "${swagger.model.channel.details.flagIo}")
    private Boolean flagIo;

    @JsonProperty("agid")
    @ApiModelProperty(value = "${swagger.model.channel.details.agid}")
    @NotNull
    private Boolean agid;

    @JsonProperty("payment_types")
    @ApiModelProperty(value = "${swagger.model.PspChannelPaymentTypesResource.list}")
    private List<String> paymentTypeList;

    @JsonProperty("primitive_version")
    @ApiModelProperty(value = "${swagger.model.channel.details.primitiveVersion}")
    private String primitiveVersion;

    @JsonProperty("target_host_nmp")
    @ApiModelProperty(value = "${swagger.model.channel.details.targetHostNmp}")
    private String targetHostNmp;

    @JsonProperty("target_port_nmp")
    @ApiModelProperty(value = "${swagger.model.channel.details.targetPortNmp}")
    private String targetPortNmp;

    @JsonProperty("target_path_nmp")
    @ApiModelProperty(value = "${swagger.model.channel.details.targetPathNmp}")
    private String targetPathNmp;

}
