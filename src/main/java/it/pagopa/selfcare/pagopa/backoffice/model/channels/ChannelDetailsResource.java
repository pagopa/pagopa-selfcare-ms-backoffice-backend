package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentModel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Protocol;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Data
public class ChannelDetailsResource extends ChannelResource {

    @JsonProperty("password")
    @ApiModelProperty(value = " channel's password")
    private String password;

    @JsonProperty("new_password")
    @ApiModelProperty(value = " channel's new password")
    private String newPassword;

    @JsonProperty("protocol")
    @ApiModelProperty(value = " channel's protocol")
    private Protocol protocol;

    @JsonProperty("ip")
    @ApiModelProperty(value = " channel's ip")
    private String ip;

    @JsonProperty("port")
    @ApiModelProperty(value = " channel's port")
    private Long port;

    @JsonProperty("service")
    @ApiModelProperty(value = " channel's service")
    private String service;

    @JsonProperty("broker_psp_code")
    @ApiModelProperty(value = " psp code")
    private String brokerPspCode;

    @JsonProperty("proxy_enabled")
    @ApiModelProperty(value = " proxy Enabled")
    private Boolean proxyEnabled;

    @JsonProperty("proxy_host")
    @ApiModelProperty(value = " proxy Host")
    private String proxyHost;


    @JsonProperty("proxy_port")
    @ApiModelProperty(value = " proxy Port")
    private Long proxyPort;

    @JsonProperty("proxy_username")
    @ApiModelProperty(value = " proxy Username")
    private String proxyUsername;

    @ToString.Exclude
    @JsonProperty("proxy_password")
    @ApiModelProperty(value = " proxy Password")
    private String proxyPassword;

    @JsonProperty("target_host")
    @ApiModelProperty(value = " target host")
    private String targetHost;

    @JsonProperty("target_port")
    @ApiModelProperty(value = " target port")
    private Long targetPort;

    @JsonProperty("target_path")
    @ApiModelProperty(value = " target path's")
    private String targetPath;

    @JsonProperty("thread_number")
    @ApiModelProperty(value = " thread number")
    private Long threadNumber;

    @JsonProperty("timeout_a")
    @ApiModelProperty(value = " timeout A")
    private Long timeoutA;

    @JsonProperty("timeout_b")
    @ApiModelProperty(value = " timeout B")
    private Long timeoutB;

    @JsonProperty("timeout_c")
    @ApiModelProperty(value = " timeout C")
    private Long timeoutC;

    @JsonProperty("nmp_service")
    @ApiModelProperty(value = " nmp service")
    private String nmpService;

    @JsonProperty("new_fault_code")
    @ApiModelProperty(value = " new fault code")
    private Boolean newFaultCode;

    // CANALI_NODO

    @JsonProperty("redirect_ip")
    @ApiModelProperty(value = " redirect ip")
    private String redirectIp;

    @JsonProperty("redirect_path")
    @ApiModelProperty(value = " redirect path")
    private String redirectPath;


    @JsonProperty("redirect_port")
    @ApiModelProperty(value = " redirect port")
    private Long redirectPort;

    @JsonProperty("redirect_query_string")
    @ApiModelProperty(value = " redirect query string")
    private String redirectQueryString;

    @JsonProperty("redirect_protocol")
    @ApiModelProperty(value = " redirect protocol")
    private Protocol redirectProtocol;

    @JsonProperty("payment_model")
    @ApiModelProperty(value = " payment model")
    private PaymentModel paymentModel;

    @JsonProperty("serv_plugin")
    @ApiModelProperty(value = " service plugin")
    private String servPlugin;

    @JsonProperty("rt_push")
    @ApiModelProperty(value = " rt Push")
    private Boolean rtPush;

    @JsonProperty("on_us")
    @ApiModelProperty(value = " on us")
    private Boolean onUs;

    @JsonProperty("card_chart")
    @ApiModelProperty(value = " card chart")
    private Boolean cardChart;

    @JsonProperty("recovery")
    @ApiModelProperty(value = " recovery")
    private Boolean recovery;

    @JsonProperty("digital_stamp_brand")
    @ApiModelProperty(value = " digital stamp brand")
    private Boolean digitalStampBrand;

    @JsonProperty("flag_io")
    @ApiModelProperty(value = " flag io")
    private Boolean flagIo;

    @JsonProperty("agid")
    @ApiModelProperty(value = " agid")
    private Boolean agid;

    @JsonProperty("payment_types")
    @ApiModelProperty(value = " List of payment types")
    private List<String> paymentTypeList;

    @JsonProperty("primitive_version")
    @ApiModelProperty(value = "primitive version")
    private Integer primitiveVersion;

    @JsonProperty("target_host_nmp")
    @ApiModelProperty(value = " target host NMP")
    private String targetHostNmp;

    @JsonProperty("target_port_nmp")
    @ApiModelProperty(value = " target port NMP")
    private String targetPortNmp;

    @JsonProperty("target_path_nmp")
    @ApiModelProperty(value = " target path NMP")
    private String targetPathNmp;

    @ApiModelProperty(" created by")
    private String createdBy;
    @ApiModelProperty(" modified by")
    private String modifiedBy;
    @ApiModelProperty(value = "channel's validation status")
    private WrapperStatus wrapperStatus;
    @ApiModelProperty(" creation date")
    private Instant createdAt;
    @ApiModelProperty(" modification date")
    private Instant modifiedAt;

}
