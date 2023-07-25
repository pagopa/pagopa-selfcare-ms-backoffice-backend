package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PaymentModel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Protocol;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
public class ChannelDetailsDto {

    @JsonProperty("psp_email")
    @ApiModelProperty(value = "${swagger.model.channel.pspEmail}")
    private String pspEmail;

    @JsonProperty("channel_code")
    @ApiModelProperty(value = "${swagger.model.channel.code}")
    private String channelCode;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("broker_description")
    @ApiModelProperty(value = "${swagger.model.broker.description}")
    private String brokerDescription;

    @JsonProperty("password")
    @ApiModelProperty(value = "${swagger.model.channel.details.password}")
    private String password;

    @JsonProperty("new_password")
    @ApiModelProperty(value = "${swagger.model.channel.details.newPassword}")
    private String newPassword;

    @JsonProperty("protocol")
    @ApiModelProperty(value = "${swagger.model.channel.details.protocol}")
    private Protocol protocol;

    @JsonProperty("ip")
    @ApiModelProperty(value = "${swagger.model.channel.details.ip}")
    private String ip;

    @JsonProperty("port")
    @ApiModelProperty(value = "${swagger.model.channel.details.port}")
    private Long port;

    @JsonProperty("service")
    @ApiModelProperty(value = "${swagger.model.channel.details.service}")
    private String service;

    @JsonProperty("broker_psp_code")
    @ApiModelProperty(value = "${swagger.model.channel.details.brokerPspCode}")
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
    private Long threadNumber;


    @JsonProperty("timeout_a")
    @ApiModelProperty(value = "${swagger.model.channel.details.timeoutA}")
    private Long timeoutA;

    @JsonProperty("timeout_b")
    @ApiModelProperty(value = "${swagger.model.channel.details.timeoutB}")
    private Long timeoutB;

    @JsonProperty("timeout_c")
    @ApiModelProperty(value = "${swagger.model.channel.details.timeoutC}")
    private Long timeoutC;

    @JsonProperty("nmp_service")
    @ApiModelProperty(value = "${swagger.model.channel.details.nmpService}")
    private String nmpService;

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
    private PaymentModel paymentModel;

    @JsonProperty("serv_plugin")
    @ApiModelProperty(value = "${swagger.model.channel.details.servPlugin}")
    private String servPlugin;

    @JsonProperty("rt_push")
    @ApiModelProperty(value = "${swagger.model.channel.details.rtPush}")
    private Boolean rtPush;

    @JsonProperty("on_us")
    @ApiModelProperty(value = "${swagger.model.channel.details.onUs}")
    private Boolean onUs;

    @JsonProperty("card_chart")
    @ApiModelProperty(value = "${swagger.model.channel.details.cardChart}")
    private Boolean cardChart;

    @JsonProperty("recovery")
    @ApiModelProperty(value = "${swagger.model.channel.details.recovery}")
    private Boolean recovery;

    @JsonProperty("digital_stamp_brand")
    @ApiModelProperty(value = "${swagger.model.channel.details.digitalStampBrand}")
    private Boolean digitalStampBrand;

    @JsonProperty("flag_io")
    @ApiModelProperty(value = "${swagger.model.channel.details.flagIo}")
    private Boolean flagIo;

    @JsonProperty("agid")
    @ApiModelProperty(value = "${swagger.model.channel.details.agid}")
    private Boolean agid;

    @JsonProperty("payment_types")
    @ApiModelProperty(value = "${swagger.model.PspChannelPaymentTypesResource.list}")
    private List<String> paymentTypeList;

    @JsonProperty("primitive_version")
    @ApiModelProperty(value = "${swagger.model.channel.details.primitiveVersion}")
    private Integer primitiveVersion;

    @ApiModelProperty(value = "${swagger.model.channel.details.note}")
    private String note = "";

    @ApiModelProperty(value = "${swagger.model.channel.details.status}")
    private WrapperStatus status = WrapperStatus.TO_CHECK;

    @JsonProperty(required = true)
    @NotBlank
    @ApiModelProperty(value = "${swagger.model.channel.details.validationUrl}")
    private String validationUrl;

    @ApiModelProperty(value = "${swagger.model.channel.details.flagPspCp}")
    private Boolean flagPspCp = false;

    public String getEmail(){
        String environment = System.getenv("env")!=null?System.getenv("env"):"local";
        return environment.equals("prod")? getPspEmail():System.getenv("TEST_EMAIL") ;
    }
}
