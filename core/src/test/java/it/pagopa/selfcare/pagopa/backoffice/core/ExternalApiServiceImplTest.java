package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.TestUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Attribute;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static it.pagopa.selfcare.pagopa.TestUtils.reflectionEqualsByName;
import static it.pagopa.selfcare.pagopa.backoffice.core.ExternalApiServiceImpl.AN_INSTITUTION_ID_IS_REQUIRED;
import static it.pagopa.selfcare.pagopa.backoffice.core.ExternalApiServiceImpl.A_PRODUCT_ID_IS_REQUIRED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ExternalApiServiceImpl.class)
class ExternalApiServiceImplTest {
    
    @Autowired
    private ExternalApiServiceImpl externalApiService;
    
    @MockBean
    private ExternalApiConnector externalApiConnectorMock;
    
    @Test
    void getInstitution_nullInstitutionId(){
        //given
        final String institutionId = null;
        //when
        Executable executable = () -> externalApiService.getInstitution(institutionId);
        //then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(AN_INSTITUTION_ID_IS_REQUIRED, e.getMessage());
        verifyNoInteractions(externalApiConnectorMock);
    }
    
    @Test
    void getInstitution(){
        //given
        final String institutionId = "institutionId";
        Institution institutionMock = mockInstance(new Institution());
        Attribute attributeMock = mockInstance(new Attribute());
        institutionMock.setAttributes(List.of(attributeMock));
        when(externalApiConnectorMock.getInstitution(any()))
                .thenReturn(institutionMock);
        //when
        Institution institution = externalApiService.getInstitution(institutionId);
        //then
        assertNotNull(institution);
        reflectionEqualsByName(institutionMock, institution);
        verify(externalApiConnectorMock, times(1))
                .getInstitution(institutionId);
        verifyNoMoreInteractions(externalApiConnectorMock);
    }
    
    @Test
    void getInstitutions_nullProductId(){
        //given
        String productId = null;
        //when
        Executable executable = () -> externalApiService.getInstitutions(productId);
        //then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(A_PRODUCT_ID_IS_REQUIRED, e.getMessage());
        verifyNoInteractions(externalApiConnectorMock);
    }
    
    @Test
    void getInstitutions(){
        //given
        String productId = "productId";
        InstitutionInfo institutionInfo = mockInstance(new InstitutionInfo());
        institutionInfo.setUserProductRoles(List.of("productRole"));
        when(externalApiConnectorMock.getInstitutions(any()))
                .thenReturn(List.of(institutionInfo));
        //when
        Collection<InstitutionInfo> institutionInfos = externalApiService.getInstitutions(productId);
        //then
        assertNotNull(institutionInfos);
        institutionInfos.forEach(TestUtils::checkNotNullFields);
        institutionInfos.forEach(institutionInfo1 -> TestUtils.reflectionEqualsByName(institutionInfo, institutionInfo1));
        verify(externalApiConnectorMock, times(1))
                .getInstitutions(productId);
        verifyNoMoreInteractions(externalApiConnectorMock);
    }
    
    @Test
    void getInstitutionUserProducts_nullInstitutionId(){
        //given
        String institutionId = null;
        //when
        Executable executable = () -> externalApiService.getInstitutionUserProducts(institutionId);
        //then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(AN_INSTITUTION_ID_IS_REQUIRED, e.getMessage());
        verifyNoInteractions(externalApiConnectorMock);
    }
    
    @Test
    void getInstitutionUserProducts(){
        //given
        String institutionId = "institutionId";
        Product productMock = mockInstance(new Product());
        when(externalApiConnectorMock.getInstitutionUserProducts(any()))
                .thenReturn(List.of(productMock));
        //when
        List<Product> products = externalApiService.getInstitutionUserProducts(institutionId);
        //then
        assertNotNull(products);
        products.forEach(TestUtils::checkNotNullFields);
        products.forEach(product -> TestUtils.reflectionEqualsByName(productMock, product));
        verify(externalApiConnectorMock, times(1))
                .getInstitutionUserProducts(institutionId);
    }

}
