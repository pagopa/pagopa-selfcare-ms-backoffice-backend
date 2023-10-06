package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomies;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomy;

import java.util.List;

public interface TaxonomyConnector {

    List<Taxonomy> getTaxonomies();
}
