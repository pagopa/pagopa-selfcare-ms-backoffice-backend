package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.product.Product;
import it.pagopa.selfcare.pagopa.backoffice.web.model.products.ProductsResource;

public class ProductMapper {
    
    public static ProductsResource toResource(Product model){
        ProductsResource resource = null;
        if (model != null){
            resource = new ProductsResource();
            resource.setId(model.getId());
            resource.setDescription(model.getDescription());
            resource.setTitle(model.getTitle());
            resource.setUrlBO(model.getUrlBO());
            resource.setUrlPublic(model.getUrlPublic());
        }
        return resource;
    }
}
