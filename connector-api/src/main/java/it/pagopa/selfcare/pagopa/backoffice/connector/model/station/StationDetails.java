package it.pagopa.selfcare.pagopa.backoffice.connector.model.station;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Protocol;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StationDetails extends Station {

    @JsonProperty("broker_details")
    @NotBlank
    private BrokerDetails intermediarioPa;

    @JsonProperty("ip")
    private String ip;
    @JsonProperty("new_password")
    private String newPassword;

    @JsonProperty("password")
    private String password;

    @JsonProperty("port")
    @NotNull
    private Long port;

    @JsonProperty("protocol")
    @NotNull
    private Protocol protocol;

    @JsonProperty("redirect_ip")
    private String redirectIp;

    @JsonProperty("redirect_path")
    private String redirectPath;

    @JsonProperty("redirect_port")
    private Long redirectPort;

    @JsonProperty("redirect_query_string")
    private String redirectQueryString;

    @JsonProperty("redirect_protocol")
    private Protocol redirectProtocol;

    @JsonProperty("service")
    private String service;

    @JsonProperty("pof_service")
    private String pofService;

    @JsonProperty("broker_code")
    @NotBlank
    private String brokerCode;

    @JsonProperty("protocol_4mod")
    private Protocol protocol4Mod;

    @JsonProperty("ip_4mod")
    private String ip4Mod;

    @JsonProperty("port_4mod")
    private Long port4Mod;

    @JsonProperty("service_4mod")
    private String service4Mod;

    @JsonProperty("proxy_enabled")
    private Boolean proxyEnabled;

    @JsonProperty("proxy_host")
    private String proxyHost;

    @JsonProperty("proxy_port")
    private Long proxyPort;

    @JsonProperty("proxy_username")
    private String proxyUsername;

    @JsonProperty("proxy_password")
    private String proxyPassword;

    @JsonProperty("thread_number")
    @NotNull
    private Long threadNumber;

    @JsonProperty("timeout_a")
    @NotNull
    private Long timeoutA;

    @JsonProperty("timeout_b")
    @NotNull
    private Long timeoutB;

    @JsonProperty("timeout_c")
    @NotNull
    private Long timeoutC;

    @JsonProperty("flag_online")
    private Boolean flagOnline;

    @JsonIgnore
    private Long brokerObjId;

    @JsonProperty("invio_rt_istantaneo")
    private Boolean rtInstantaneousDispatch;

    @JsonProperty("target_host")
    private String targetHost;

    @JsonProperty("target_port")
    private Long targetPort;

    @JsonProperty("target_path")
    private String targetPath;

    @JsonProperty("primitive_version")
    private Integer primitiveVersion;

    @JsonProperty("target_host_pof")
    private String targetHostPof;

    @JsonProperty("target_port_pof")
    private Long targetPortPof;

    @JsonProperty("target_path_pof")
    private String targetPathPof;

}
