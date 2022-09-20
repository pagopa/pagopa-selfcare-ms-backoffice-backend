package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.product.Product;
import it.pagopa.selfcare.pagopa.backoffice.web.model.products.ProductsResource;
import org.junit.jupiter.api.Test;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static it.pagopa.selfcare.pagopa.TestUtils.reflectionEqualsByName;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductMapperTest {
    
    @Test
    void toProductResource_null(){
        //given
        Product model = null;
        //when
        ProductsResource resource = ProductMapper.toResource(model);
        //then
        assertNull(resource);
    }
    
    @Test
    void toProductResource(){
        //given
        Product model = mockInstance(new Product());
        //when
        ProductsResource resource = ProductMapper.toResource(model);
        //then
        assertNotNull(resource);
        reflectionEqualsByName(model, resource);
    }

}
