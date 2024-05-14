package it.pagopa.selfcare.pagopa.backoffice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entity class that contains the maintenance messages
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document("maintenance")
@FieldNameConstants(onlyExplicitlyIncluded = true)
public class MaintenanceEntity {

    @Id
    private String id;
    private String bannerMessage;
    private String pageMessage;
}
