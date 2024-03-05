package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.TaxonomyClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.TaxonomyGroupEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.*;
import it.pagopa.selfcare.pagopa.backoffice.repository.TaxonomyGroupRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.TaxonomyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Instance of taxonomy related services
 */
@Service
public class TaxonomyService {

    @Autowired
    private TaxonomyRepository taxonomyRepository;

    @Autowired
    private TaxonomyGroupRepository taxonomyGroupRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Taxonomies getTaxonomies(String code, String ec, String macroArea, Boolean onlyValid) {
        List<Taxonomy> taxonomies = taxonomyRepository.searchTaxonomies(
                ec, macroArea, code != null ? ".*".concat(code).concat(".*") : null, onlyValid, Instant.now())
                .stream()
                .map(elem -> modelMapper.map(elem, Taxonomy.class))
                .toList();
        return Taxonomies.builder()
                .taxonomies(taxonomies)
                .build();
    }

    /**
     * Method to return the list of available taxonomy groyp saved on cosmos
     * @return instance of TaxonomyGroups, containing the list of available taxonomy groups
     */
    public TaxonomyGroups getTaxonomyGroups() {
        List<TaxonomyGroupEntity> taxonomyGroupEntities = taxonomyGroupRepository.findAll();
        TaxonomyGroups taxonomyGroups = new TaxonomyGroups();
        taxonomyGroups.setTaxonomyGroups(
                taxonomyGroupEntities.stream().map(taxonomyGroupEntity -> {
                    TaxonomyGroup taxonomyGroup = new TaxonomyGroup();
                    BeanUtils.copyProperties(taxonomyGroupEntity, taxonomyGroup);
                    taxonomyGroup.setAreas(taxonomyGroupEntity.getAreas().stream().map(area -> {
                        TaxonomyGroupArea taxonomyGroupArea = new TaxonomyGroupArea();
                        BeanUtils.copyProperties(area, taxonomyGroupArea);
                        return taxonomyGroupArea;
                    }).collect(Collectors.toSet()));
                    return taxonomyGroup;
                }).collect(Collectors.toList())
        );
        return taxonomyGroups;
    }

}
