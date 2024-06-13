package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CIBundleAttributeResource {

    private List<CIBundleAttributeModel> attributes;
}
