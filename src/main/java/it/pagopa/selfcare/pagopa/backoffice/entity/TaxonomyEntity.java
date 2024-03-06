package it.pagopa.selfcare.pagopa.backoffice.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document("taxonomies")
@FieldNameConstants(onlyExplicitlyIncluded = true)
public class TaxonomyEntity implements Persistable<String> {

    @Id
    private String id;

    private String ecTypeCode;

    private String ecType;

    private String macroAreaEcProgressive;

    private String macroAreaName;

    private String macroAreaDescription;

    private String serviceTypeCode;

    private String serviceType;

    private String legalReasonCollection;

    private String serviceTypeDescription;

    private String taxonomyVersion;

    @Indexed(unique = true)
    private String specificBuiltInData;

    private Instant startDate;

    private Instant endDate;

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

    public TaxonomyEntity() {
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
