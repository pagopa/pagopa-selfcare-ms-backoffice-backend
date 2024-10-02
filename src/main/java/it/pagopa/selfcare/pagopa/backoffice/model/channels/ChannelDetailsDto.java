package it.pagopa.selfcare.pagopa.backoffice.model.channels;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentModel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Protocol;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelDetailsDto {

    @JsonProperty("psp_email")
    @Schema(description = " email of the payment service provider")
    private String pspEmail;

    @JsonProperty("channel_code")
    @Schema(description = "Channel code")
    private String channelCode;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("broker_description")
    @Schema(description = "Broker description. Read only field")
    private String brokerDescription;

    @JsonProperty("password")
    @Schema(description = " channel's password")
    private String password;

    @JsonProperty("new_password")
    @Schema(description = " channel's new password")
    private String newPassword;

    @JsonProperty("protocol")
    @Schema(description = " channel's protocol")
    private Protocol protocol;

    @JsonProperty("ip")
    @Schema(description = " channel's ip")
    private String ip;

    @JsonProperty("port")
    @Schema(description = " channel's port")
    private Long port;

    @JsonProperty("service")
    @Schema(description = " channel's service")
    private String service;

    @JsonProperty("broker_psp_code")
    @Schema(description = " psp code")
    private String brokerPspCode;

    @JsonProperty("proxy_enabled")
    @Schema(description = " proxy Enabled")
    private Boolean proxyEnabled;

    @JsonProperty("proxy_host")
    @Schema(description = " proxy Host")
    private String proxyHost;

    @JsonProperty("proxy_port")
    @Schema(description = " proxy Port")
    private Long proxyPort;

    @JsonProperty("proxy_username")
    @Schema(description = " proxy Username")
    private String proxyUsername;

    @ToString.Exclude
    @JsonProperty("proxy_password")
    @Schema(description = " proxy Password")
    private String proxyPassword;

    @JsonProperty("target_host")
    @Schema(description = " target host")
    private String targetHost;

    @JsonProperty("target_port")
    @Schema(description = " target port")
    private Long targetPort;

    @JsonProperty("target_path")
    @Schema(description = " target path's")
    private String targetPath;

    @JsonProperty("thread_number")
    @Schema(description = " thread number")
    private Long threadNumber;


    @JsonProperty("timeout_a")
    @Schema(description = " timeout A")
    private Long timeoutA;

    @JsonProperty("timeout_b")
    @Schema(description = " timeout B")
    private Long timeoutB;

    @JsonProperty("timeout_c")
    @Schema(description = " timeout C")
    private Long timeoutC;

    @JsonProperty("nmp_service")
    @Schema(description = " nmp service")
    private String nmpService;

    @JsonProperty("new_fault_code")
    @Schema(description = " new fault code")
    private Boolean newFaultCode;

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
    private Protocol redirectProtocol;

    @JsonProperty("payment_model")
    @Schema(description = " payment model")
    private PaymentModel paymentModel;

    @JsonProperty("serv_plugin")
    @Schema(description = " service plugin")
    private String servPlugin;

    @JsonProperty("rt_push")
    @Schema(description = " rt Push")
    private Boolean rtPush;

    @JsonProperty("on_us")
    @Schema(description = " on us")
    private Boolean onUs;

    @JsonProperty("card_chart")
    @Schema(description = " card chart")
    private Boolean cardChart;

    @JsonProperty("recovery")
    @Schema(description = " recovery")
    private Boolean recovery;

    @JsonProperty("digital_stamp_brand")
    @Schema(description = " digital stamp brand")
    private Boolean digitalStampBrand;

    @JsonProperty("flag_io")
    @Schema(description = " flag io")
    private Boolean flagIo;

    @JsonProperty("agid")
    @Schema(description = " agid")
    private Boolean agid;

    @JsonProperty("payment_types")
    @Schema(description = " List of payment types")
    private List<String> paymentTypeList;

    @JsonProperty("primitive_version")
    @Schema(description = "primitive version")
    private Integer primitiveVersion;

    @Schema(description = "channel note description by operation team")
    private String note = "";

    @Schema(description = "channel's validation status")
    private WrapperStatus status = WrapperStatus.TO_CHECK;

    @JsonProperty(required = true)
    @NotBlank
    @Schema(description = "Url jira for ChannelDetail validation")
    private String validationUrl;

    @Schema(description = "Represents the authorization to carry out the transfer of the information present in additional payment information in the tags relating to payment by card for the PA in V1")
    private Boolean flagPspCp = false;

    @JsonProperty("flag_standin")
    @Schema(description = "Represents the authorization to use the standin mode with this station")
    @NotNull
    private Boolean flagStandin;

}
