package it.pagopa.selfcare.pagopa.backoffice.entity;

import it.pagopa.selfcare.pagopa.backoffice.util.IbanDeletionRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Instant;

@Document(collection = "ibanDeletionRequests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IbanDeletionRequestEntity {

    @Id
    @Field(targetType = FieldType.STRING)
    private String id;
    @CreatedDate
    private Instant requestedAt;
    @LastModifiedDate
    private Instant updatedAt;
    private Instant scheduledExecutionDate;
    @Builder.Default
    private IbanDeletionRequestStatus status = IbanDeletionRequestStatus.PENDING;
    private String creditorInstitutionCode;
    private String ibanValue;
}