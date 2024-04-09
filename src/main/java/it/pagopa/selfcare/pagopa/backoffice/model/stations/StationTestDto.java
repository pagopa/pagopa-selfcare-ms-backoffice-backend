package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StationTestDto implements Serializable {

    private String hostUrl;
    private String hostPath;
    private Integer hostPort;

}
