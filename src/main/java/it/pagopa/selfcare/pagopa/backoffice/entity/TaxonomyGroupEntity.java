package it.pagopa.selfcare.pagopa.backoffice.entity;


import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document("taxonomy_groups")
@FieldNameConstants(onlyExplicitlyIncluded = true)
public class TaxonomyGroupEntity implements Persistable<String> {

    @Id
    private String id;

    private String ecTypeCode;

    private String ecType;

    private Set<TaxonomyGroupAreaEntity> areas;

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

    public TaxonomyGroupEntity() {
        createdAt = Instant.now();
    }

    @Override
    public boolean isNew() {
        return false;
    }


    public static class Fields {

        protected static String id = org.springframework.data.mongodb.core.aggregation.Fields.UNDERSCORE_ID;

        private Fields() {
        }
    }

}
