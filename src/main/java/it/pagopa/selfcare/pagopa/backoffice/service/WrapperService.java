package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class WrapperService {

    @Autowired
    private WrapperRepository repository;

    @Autowired
    private AuditorAware<String> auditorAware;

    public static List<WrapperEntityOperations> getWrapperEntityOperationsSortedList(WrapperEntities wrapperEntities) {
        List<WrapperEntityOperations> list = new ArrayList<>(wrapperEntities.getEntities());
        list.sort(Comparator.comparing(WrapperEntityOperations::getCreatedAt, Comparator.reverseOrder()));
        return list;

    }

    public static void updateCurrentWrapperEntity(WrapperEntities wrapperEntities, WrapperEntityOperations wrapperEntity, String status, String note, String modifiedByOpt) {
        wrapperEntities.getEntities().sort(Comparator.comparing((WrapperEntityOperations t) -> t.getCreatedAt(), Comparator.reverseOrder()));

        WrapperEntity wrapper = (WrapperEntity) wrapperEntities.getEntities().get(0);
        wrapperEntities.setStatus(WrapperStatus.valueOf(status));
        wrapper.setEntity(wrapperEntity.getEntity());
        wrapperEntities.setModifiedAt(Instant.now());
        wrapperEntities.setModifiedByOpt(modifiedByOpt);
        wrapper.setNote(note);
        wrapper.setModifiedAt(Instant.now());
        wrapper.setModifiedByOpt(modifiedByOpt);
        wrapper.setStatus(WrapperStatus.valueOf(status));
    }

    public WrapperEntities<ChannelDetails> insert(ChannelDetails channelDetails, String note, String status) {
        WrapperEntity<ChannelDetails> wrapperEntity = new WrapperEntity<>(channelDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        WrapperEntities<ChannelDetails> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        wrapperEntities.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        String createdBy = wrapperEntities.getCreatedBy();
        WrapperEntities<ChannelDetails> response = null;
        try {
            wrapperEntities.setCreatedBy(auditorAware.getCurrentAuditor().orElse(null));
            response = repository.insert(wrapperEntities);
        } catch (DuplicateKeyException e) {
            response = update(channelDetails, note, status, createdBy);
        }
        return response;
    }

    public WrapperEntities<StationDetails> insert(StationDetails stationDetails, String note, String status) {
        WrapperEntity<StationDetails> wrapperEntity = new WrapperEntity<>(stationDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        WrapperEntities<StationDetails> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        wrapperEntities.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        String createdBy = wrapperEntities.getCreatedBy();
        WrapperEntities<StationDetails> response = null;
        try {
            wrapperEntities.setCreatedBy(auditorAware.getCurrentAuditor().orElse(null));
            response = repository.insert(wrapperEntities);
        } catch (DuplicateKeyException e) {
            response = update(stationDetails, note, status, createdBy);
        }
        return response;
    }

    public WrapperEntities<ChannelDetails> update(ChannelDetails channelDetails, String note, String status, String createdBy) {
        String channelCode = channelDetails.getChannelCode();
        Optional<WrapperEntities> opt = repository.findById(channelCode);
        if(opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        WrapperEntities<ChannelDetails> wrapperEntities = (WrapperEntities) opt.get();
        wrapperEntities.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        WrapperEntity<ChannelDetails> wrapperEntity = new WrapperEntity<>(channelDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        wrapperEntity.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        wrapperEntities.setStatus(WrapperStatus.valueOf(status));
        wrapperEntities.getEntities().add(wrapperEntity);
        if(createdBy != null)
            wrapperEntities.setCreatedBy(createdBy);
        return repository.save(wrapperEntities);

    }

    public WrapperEntities<ChannelDetails> updateByOpt(ChannelDetails channelDetails, String note, String status) {
        String channelCode = channelDetails.getChannelCode();
        Optional<WrapperEntities> opt = repository.findById(channelCode);
        if(opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        WrapperEntities<ChannelDetails> wrapperEntities = (WrapperEntities) opt.get();
        String modifiedByOpt = auditorAware.getCurrentAuditor().orElse(null);
        WrapperEntity<ChannelDetails> wrapperEntity = new WrapperEntity<>(channelDetails);
        wrapperEntity.setModifiedByOpt(modifiedByOpt);
        updateCurrentWrapperEntity(wrapperEntities, new WrapperEntity<>(channelDetails), status, note, modifiedByOpt);
        return repository.save(wrapperEntities);
    }

    public WrapperEntities<StationDetails> updateByOpt(StationDetails stationDetails, String note, String status) {
        String stationCode = stationDetails.getStationCode();
        Optional<WrapperEntities> opt = repository.findById(stationCode);
        if(opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        WrapperEntities<StationDetails> wrapperEntities = (WrapperEntities) opt.get();
        String modifiedByOpt = auditorAware.getCurrentAuditor().orElse(null);
        WrapperEntity<StationDetails> wrapperEntity = new WrapperEntity<>(stationDetails);
        wrapperEntity.setModifiedByOpt(modifiedByOpt);
        updateCurrentWrapperEntity(wrapperEntities, wrapperEntity, status, note, modifiedByOpt);
        return repository.save(wrapperEntities);
    }

    public WrapperEntities<StationDetails> update(StationDetails stationDetails, String note, String status, String createdBy) {
        String stationCode = stationDetails.getStationCode();
        Optional<WrapperEntities> opt = repository.findById(stationCode);
        if(opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        WrapperEntities<StationDetails> wrapperEntities = (WrapperEntities) opt.get();
        WrapperEntity<StationDetails> wrapperEntity = new WrapperEntity<>(stationDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        wrapperEntities.setStatus(WrapperStatus.valueOf(status));
        wrapperEntities.getEntities().add(wrapperEntity);
        if(createdBy != null)
            wrapperEntities.setCreatedBy(createdBy);
        return repository.save(wrapperEntities);
    }

    public WrapperEntitiesList findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus status, WrapperType wrapperType, String brokerCode, String idLike, Integer page, Integer size, String sorting) {

        Sort sort;
        if("DESC".equalsIgnoreCase(sorting)) {
            sort = Sort.by(Sort.Order.desc("id"));
        } else {
            sort = Sort.by(Sort.Order.asc("id"));
        }

        Pageable paging = PageRequest.of(page, size, sort);
        Page<WrapperEntities<?>> response = null;

        int switchCase = (brokerCode != null ? 1 : 0) | (idLike != null ? 2 : 0);
        if(status != null) {
            switch (switchCase) {
                case 0:
                    response = repository.findByStatusAndType(status, wrapperType, paging);
                    break;
                case 1:
                    response = repository.findByStatusAndTypeAndBrokerCode(status, wrapperType, brokerCode, paging);
                    break;
                case 2:
                    response = repository.findByStatusAndTypeAndIdLike(status, wrapperType, idLike, paging);
                    break;
                case 3:
                    response = repository.findByStatusAndTypeAndBrokerCodeAndIdLike(status, wrapperType, brokerCode, idLike, paging);
                    break;
                default:
                    // Gestisci caso non previsto
                    break;
            }
        } else {
            switch (switchCase) {
                case 0:
                    response = repository.findByType(wrapperType, paging);
                    break;
                case 1:
                    response = repository.findByTypeAndBrokerCode(wrapperType, brokerCode, paging);
                    break;
                case 2:
                    response = repository.findByTypeAndIdLike(wrapperType, idLike, paging);
                    break;
                case 3:
                    response = repository.findByTypeAndBrokerCodeAndIdLike(wrapperType, brokerCode, idLike, paging);
                    break;
                default:
                    // Gestisci caso non previsto
                    break;
            }
        }

        return WrapperEntitiesList.builder()
                .wrapperEntities(response.getContent())
                .pageInfo(PageInfo.builder()
                        .page(page)
                        .limit(size)
                        .totalPages(response.getTotalPages())
                        .itemsFound(response.getNumberOfElements())
                        .build())
                .build();
    }

    public <T> WrapperEntities<T> findById(String id) {
        var response = repository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        response.sortEntitesByCreatedAt();
        return response;
    }

    public WrapperEntitiesList findByIdLikeOrTypeOrBrokerCode(String idLike, WrapperType wrapperType, String brokerCode, Integer page, Integer size) {

        Pageable paging = PageRequest.of(page, size);
        Page<WrapperEntities<?>> response;

        if(brokerCode == null && idLike == null) {
            response = repository.findByType(wrapperType, paging);
        } else if(brokerCode == null) {
            response = repository.findByIdLikeAndType(idLike, wrapperType, paging);
        } else if(idLike == null) {
            response = repository.findByTypeAndBrokerCode(wrapperType, brokerCode, paging);
        } else {
            response = repository.findByIdLikeAndTypeAndBrokerCode(idLike, wrapperType, brokerCode, paging);
        }

        PageInfo pi = new PageInfo();
        pi.setPage(page);
        pi.setLimit(size);
        pi.setItemsFound(response.getTotalPages());
        WrapperEntitiesList wrapperEntitiesList = new WrapperEntitiesList();
        wrapperEntitiesList.setWrapperEntities(response.getContent());
        wrapperEntitiesList.setPageInfo(pi);

        return wrapperEntitiesList;
    }
}
