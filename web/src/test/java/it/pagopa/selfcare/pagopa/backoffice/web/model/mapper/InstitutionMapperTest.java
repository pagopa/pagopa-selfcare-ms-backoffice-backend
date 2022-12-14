package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Attribute;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.AttributeResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.InstitutionDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.institutions.InstitutionResource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static it.pagopa.selfcare.pagopa.TestUtils.reflectionEqualsByName;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class InstitutionMapperTest {

    @Test
    void toInstitutionDetailResource_null() {
        //given
        Institution model = null;
        //when
        InstitutionDetailResource resource = InstitutionMapper.toResource(model);
        //then
        assertNull(resource);
    }

    @Test
    void toInstitutionDetailResource() {
        //given
        Institution model = mockInstance(new Institution());
        Attribute attributeModel = mockInstance(new Attribute());
        model.setAttributes(List.of(attributeModel));
        //when
        InstitutionDetailResource resource = InstitutionMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

    @Test
    void toInstitutionResource_null() {
        //given
        InstitutionInfo model = null;
        //when
        InstitutionResource resource =InstitutionMapper.toResource(model);
        //then
        assertNull(resource);
    }
    
    @Test
    void toInstitutionResource(){
        //given
        InstitutionInfo model = mockInstance(new InstitutionInfo());
        model.setUserProductRoles(List.of("string"));
        //when
        InstitutionResource resource = InstitutionMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }
    
    @Test
    void toAttributeResource_null(){
        //given
        Attribute model = null;
        //when
        AttributeResource resource = InstitutionMapper.toResource(model);
        //then
        assertNull(resource);
    }
    
    @Test
    void toAttributeResource(){
        //given
        Attribute model = mockInstance(new Attribute());
        //when
        AttributeResource resource = InstitutionMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

}
