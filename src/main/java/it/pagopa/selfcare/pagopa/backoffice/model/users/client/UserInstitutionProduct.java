package it.pagopa.selfcare.pagopa.backoffice.model.users.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInstitutionProduct {

    private String productId;
    private String tokenId;
    private UserProductStatus status;
    private String productRole;
    private String productRoleLabel;
    private String role;
    private String env;
    private String createdAt;
    private String updatedAt;

}
