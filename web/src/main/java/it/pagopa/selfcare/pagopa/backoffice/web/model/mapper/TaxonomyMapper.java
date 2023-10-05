package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomies;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomy;
import it.pagopa.selfcare.pagopa.backoffice.web.model.taxonomies.TaxonomiesResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.taxonomies.TaxonomyResource;

import java.util.ArrayList;
import java.util.List;

public class TaxonomyMapper {

    public static TaxonomyResource toResource(Taxonomy model) {
        TaxonomyResource resource = null;
        if (model == null) {
            return null;
        }
        resource = new TaxonomyResource();

        resource.setEcType(model.getEcType());
        resource.setEndDate(model.getEndDate());
        resource.setEcTypeCode(model.getEcTypeCode());
        resource.setTaxonomyVersion(model.getTaxonomyVersion());
        resource.setMacroAreaDescription(model.getMacroAreaDescription());
        resource.setLegalReasonCollection(model.getLegalReasonCollection());
        resource.setMacroAreaName(model.getMacroAreaName());
        resource.setStartDate(model.getStartDate());
        resource.setEndDate(model.getEndDate());
        resource.setMacroAreaEcProgressive(model.getMacroAreaEcProgressive());
        resource.setSpecificBuiltInData(model.getEcType());
        resource.setServiceType(model.getServiceType());
        resource.setServiceTypeCode(model.getServiceTypeCode());
        resource.setServiceTypeDescription(model.getServiceTypeDescription());

        ;
        return resource;
    }

    public static List<TaxonomyResource> toResource(List<Taxonomy> models) {
        List<TaxonomyResource> resource = null;

        if (models == null) {
            return null;
        }
        resource = new ArrayList<>();

        for (Taxonomy t : models) {
            resource.add(toResource(t));
        }
        return resource;
    }
}