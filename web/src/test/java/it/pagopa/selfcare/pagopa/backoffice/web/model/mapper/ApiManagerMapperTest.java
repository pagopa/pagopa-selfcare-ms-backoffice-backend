package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions.ApiKeysResource;
import org.junit.jupiter.api.Test;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApiManagerMapperTest {
    
    @Test
    void toApiKeyResource_null(){
        //given
        InstitutionApiKeys model = null;
        //when
        ApiKeysResource resource = ApiManagerMapper.toApiKeysResource(model);
        //then
        assertNull(resource);
    }
    
    @Test
    void toApiKeyResource(){
        //given
        InstitutionApiKeys model = mockInstance(new InstitutionApiKeys());
        //when
        ApiKeysResource resource = ApiManagerMapper.toApiKeysResource(model);
        //then
        assertEquals(model.getPrimaryKey(), resource.getPrimaryKey());
        assertEquals(model.getSecondaryKey(), resource.getSecondaryKey());
    }

}
