package it.pagopa.selfcare.pagopa.backoffice.connector.dao.model;


import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = "id")
@Document("tavoloOp")
@FieldNameConstants(onlyExplicitlyIncluded = true)
public class TavoloOpEntity implements TavoloOpOperations, Persistable<String> {

    @Id
    private String id;

    private String taxCode;

    private String name;

    private String referent;

    private String email;

    private String telephone;

    @LastModifiedDate
    @FieldNameConstants.Include
    private Instant modifiedAt;
    @LastModifiedBy
    @FieldNameConstants.Include
    private String modifiedBy;

    @CreatedDate
    private Instant createdAt;
    @CreatedBy
    private String createdBy;

    public TavoloOpEntity(){
        createdAt =Instant.now();
    }

    @Override
    public boolean isNew() {
        return false;
    }


    public static class Fields {

        private Fields(){
        }
        protected static String id = org.springframework.data.mongodb.core.aggregation.Fields.UNDERSCORE_ID;
    }

}
