package it.pagopa.selfcare.pagopa.backoffice.model.users.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInstitution {

    private String id;
    private String userId;
    private String institutionId;
    private String institutionDescription;
    private String institutionRootName;
    private String userMailUuid;
    private List<UserInstitutionProduct> products;

}
