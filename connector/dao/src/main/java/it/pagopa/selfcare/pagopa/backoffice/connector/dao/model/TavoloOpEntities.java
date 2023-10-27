package it.pagopa.selfcare.pagopa.backoffice.connector.dao.model;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;
import lombok.Data;
import org.springframework.data.domain.Persistable;

import java.util.List;

@Data
public class TavoloOpEntities extends TavoloOpEntity implements TavoloOpOperations, Persistable<String> {

    List<TavoloOpEntity> tavoloOpEntityList;
}
