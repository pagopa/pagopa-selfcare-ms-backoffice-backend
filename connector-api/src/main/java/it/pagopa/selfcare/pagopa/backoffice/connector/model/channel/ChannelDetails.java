package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ChannelDetails extends  Channel{


    @JsonProperty("password")
    private String password;

    @JsonProperty("new_password")
    private String newPassword;

    @JsonProperty("protocol")
    @NotNull
    private Protocol protocol;

    @JsonProperty("ip")
    private String ip;

    @Min(1)
    @Max(65535)
    @JsonProperty("port")
    @NotNull
    private Long port;

    @JsonProperty("service")
    private String service;

    @JsonProperty("broker_psp_code")
    @NotBlank
    private String brokerPspCode;

    @JsonProperty("proxy_enabled")
    private Boolean proxyEnabled;

    @JsonProperty("proxy_host")
    private String proxyHost;

    @Min(1)
    @Max(65535)
    @JsonProperty("proxy_port")
    private Long proxyPort;

    @JsonProperty("proxy_username")
    private String proxyUsername;

    @ToString.Exclude
    @JsonProperty("proxy_password")
    private String proxyPassword;

    @JsonProperty("target_host")
    private String targetHost;

    @JsonProperty("target_port")
    private Long targetPort;

    @JsonProperty("target_path")
    private String targetPath;

    @Min(1)
    @JsonProperty("thread_number")
    @NotNull
    private Long threadNumber;

    @Min(0)
    @JsonProperty("timeout_a")
    @NotNull
    private Long timeoutA;

    @Min(0)
    @JsonProperty("timeout_b")
    @NotNull
    private Long timeoutB;

    @Min(0)
    @JsonProperty("timeout_c")
    @NotNull
    private Long timeoutC;

    @JsonProperty("nmp_service")
    private String nmpService;

    @JsonProperty("new_fault_code")
    private Boolean newFaultCode;

    // CANALI_NODO

    @JsonProperty("redirect_ip")
    private String redirectIp;

    @JsonProperty("redirect_path")
    private String redirectPath;

    @Min(1)
    @Max(65535)
    @JsonProperty("redirect_port")
    private Long redirectPort;

    @JsonProperty("redirect_query_string")
    private String redirectQueryString;

    @JsonProperty("redirect_protocol")
    private Protocol redirectProtocol;

    @JsonProperty("payment_model")
    @NotNull
    private PaymentModel paymentModel;

    @JsonProperty("serv_plugin")
    private String servPlugin;

    @JsonProperty("rt_push")
    @NotNull
    private Boolean rtPush;

    @JsonProperty("on_us")
    @NotNull
    private Boolean onUs;

    @JsonProperty("card_chart")
    @NotNull
    private Boolean cardChart;

    @JsonProperty("recovery")
    @NotNull
    private Boolean recovery;

    @JsonProperty("digital_stamp_brand")
    @NotNull
    private Boolean digitalStampBrand;

    @JsonProperty("flag_io")
    private Boolean flagIo;

    @JsonProperty("agid")
    @NotNull
    private Boolean agid;

    @JsonProperty("primitive_version")
    private String primitiveVersion;

    @JsonProperty("target_host_nmp")
    private String targetHostNmp;

    @JsonProperty("target_port_nmp")
    private String targetPortNmp;

    @JsonProperty("target_path_nmp")
    private String targetPathNmp;

}
