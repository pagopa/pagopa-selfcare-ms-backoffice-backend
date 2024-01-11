package it.pagopa.selfcare.pagopa.backoffice.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "brokerCode")
@Document("brokerIbans")
@ToString
public class BrokerIbansEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String brokerCode;

    @CreatedDate
    private Instant createdAt;

    private List<BrokerIbanEntity> ibans;
}
