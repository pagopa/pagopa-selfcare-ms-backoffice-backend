package it.pagopa.selfcare.pagopa.backoffice.entity;

import lombok.Data;
import org.springframework.data.domain.Persistable;

import java.util.List;

@Data
public class TavoloOpEntities extends TavoloOpEntity implements TavoloOpOperations, Persistable<String> {

    List<TavoloOpEntity> tavoloOpEntityList;
}
