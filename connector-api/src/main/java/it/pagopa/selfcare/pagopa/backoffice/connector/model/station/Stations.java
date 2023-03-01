package it.pagopa.selfcare.pagopa.backoffice.connector.model.station;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class Stations {

    private List<Station> stationsList;
    private PageInfo pageInfo;

}
