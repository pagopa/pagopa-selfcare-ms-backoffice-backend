package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.TaxonomyClient;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.Taxonomies;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.Taxonomy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxonomyService {

    @Autowired
    private TaxonomyClient taxonomyClient;

    @Autowired
    private ModelMapper modelMapper;

    public Taxonomies getTaxonomies() {
        List<Taxonomy> taxonomies = taxonomyClient.getTaxonomies().stream()
                .map(elem -> modelMapper.map(elem, Taxonomy.class))
                .toList();
        return Taxonomies.builder()
                .taxonomies(taxonomies)
                .build();
    }

}
