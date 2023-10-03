package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.TaxonomyConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomies;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TaxonomyServiceImpl implements TaxonomyService {
    private final TaxonomyConnector taxonomyConnector;

    @Autowired
    public TaxonomyServiceImpl(TaxonomyConnector taxonomyConnector) {
        this.taxonomyConnector = taxonomyConnector;
    }

    @Override
    public List<Taxonomy> getTaxonomies() {
        List<Taxonomy> response = taxonomyConnector.getTaxonomies();
        return response;
    }
}
