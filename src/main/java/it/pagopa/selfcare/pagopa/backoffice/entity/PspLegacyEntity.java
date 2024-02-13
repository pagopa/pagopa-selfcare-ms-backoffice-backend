package it.pagopa.selfcare.pagopa.backoffice.entity;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "cf")
@Document("pspLegacy")
@FieldNameConstants(onlyExplicitlyIncluded = true)
public class PspLegacyEntity {

    @Id
    private String id;
    @Indexed(unique = true)
    @Field(name = "CF")
    private String cf;
    @Field(name = "ABI")
    private List<String> abi;
    @Field(name = "BIC")
    private List<String> bic;

}
