package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestStationResource implements Serializable {

    private TestResultEnum testResult;
    private String message;

}
