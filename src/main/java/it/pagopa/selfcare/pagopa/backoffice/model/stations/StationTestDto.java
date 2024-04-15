package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StationTestDto implements Serializable {

    @NotNull
    private String hostUrl;
    @NotNull
    private String hostPath;
    @NotNull
    private Integer hostPort;

}
