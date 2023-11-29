package it.pagopa.selfcare.pagopa.backoffice.model.connector.tavoloop;

import it.pagopa.selfcare.pagopa.backoffice.entity.TavoloOpOperations;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TavoloOpEntitiesList {
    private List<TavoloOpOperations> tavoloOpOperationsList;
}
