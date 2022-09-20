package it.pagopa.selfcare.pagopa.backoffice.connector.model.product;

import lombok.Data;

@Data
public class Product {
    
    private String id;
    private String title;
    private String description;
    private String urlPublic;
    private String urlBO;
    
}
