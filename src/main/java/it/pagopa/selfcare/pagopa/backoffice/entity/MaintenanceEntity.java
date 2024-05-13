package it.pagopa.selfcare.pagopa.backoffice.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entity class that contains the maintenance messages
 */
@Data
@EqualsAndHashCode(of = "id")
@Document("maintenance")
@FieldNameConstants(onlyExplicitlyIncluded = true)
public class MaintenanceEntity implements Persistable<String> {

    @Id
    private String id;

    private String bannerMessage;

    private String maintenancePageMessage;

    @Override
    public boolean isNew() {
        return false;
    }
}
