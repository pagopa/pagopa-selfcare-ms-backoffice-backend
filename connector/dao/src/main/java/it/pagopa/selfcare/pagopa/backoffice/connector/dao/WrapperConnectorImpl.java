package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.WrapperConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
public class WrapperConnectorImpl implements WrapperConnector {

    private final WrapperRepository repository;
    private final MongoTemplate mongoTemplate;
    private final AuditorAware<String> auditorAware;


    @Autowired
    public WrapperConnectorImpl(WrapperRepository repository, MongoTemplate mongoTemplate, AuditorAware<String> auditorAware) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.auditorAware = auditorAware;
    }


    @Override
    public WrapperEntities insert(ChannelDetails channelDetails, String note, String status) {
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<>(channelDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        WrapperEntities wrapperEntities = new WrapperEntities(wrapperEntity);
        wrapperEntities.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        WrapperEntities response = null;
        try {
            response = repository.insert(wrapperEntities);
        } catch (DuplicateKeyException e) {
            response = (WrapperEntities) update(channelDetails, note, status);
        }
        return response;
    }

    @Override
    public WrapperEntities insert(StationDetails stationDetails, String note, String status) {
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<>(stationDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        WrapperEntities<Object> wrapperEntities = new WrapperEntities(wrapperEntity);
        wrapperEntities.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        WrapperEntities<Object> response = null;
        try {
            response = repository.insert(wrapperEntities);
        } catch (DuplicateKeyException e) {
            response = (WrapperEntities) update(stationDetails, note, status);
        }
        return response;
    }

    @Override
    public WrapperEntitiesOperations<ChannelDetails> update(ChannelDetails channelDetails, String note, String status) {
        String channelCode = channelDetails.getChannelCode();
        Optional<WrapperEntitiesOperations> opt = findById(channelCode);
        if (opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        WrapperEntities<ChannelDetails> wrapperEntities = (WrapperEntities) opt.get();
        wrapperEntities.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        WrapperEntity<ChannelDetails> wrapperEntity = new WrapperEntity<>(channelDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        wrapperEntity.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        wrapperEntities.getEntities().add(wrapperEntity);
        return repository.save(wrapperEntities);

    }

    @Override
    public WrapperEntitiesOperations<ChannelDetails> updateByOpt(ChannelDetails channelDetails, String note, String status) {
        String channelCode = channelDetails.getChannelCode();
        Optional<WrapperEntitiesOperations> opt = findById(channelCode);
        if (opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        WrapperEntities<ChannelDetails> wrapperEntities = (WrapperEntities) opt.get();
        String modifiedByOpt = auditorAware.getCurrentAuditor().orElse(null);
        wrapperEntities.updateCurrentWrapperEntity(new WrapperEntity<>(channelDetails), status, note, modifiedByOpt);
        return repository.save(wrapperEntities);
    }

    @Override
    public WrapperEntitiesOperations<StationDetails> updateByOpt(StationDetails stationDetails, String note, String status) {
        String stationCode = stationDetails.getStationCode();
        Optional<WrapperEntitiesOperations> opt = findById(stationCode);
        if (opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        WrapperEntities<StationDetails> wrapperEntities = (WrapperEntities) opt.get();
        String modifiedByOpt = auditorAware.getCurrentAuditor().orElse(null);
        WrapperEntity<StationDetails> wrapperEntity = new WrapperEntity(stationDetails);
        wrapperEntity.setModifiedByOpt(modifiedByOpt);
        wrapperEntities.updateCurrentWrapperEntity(wrapperEntity, status, note, modifiedByOpt);
        return repository.save(wrapperEntities);
    }

    @Override
    public WrapperEntitiesOperations<StationDetails> update(StationDetails stationDetails, String note, String status) {
        String stationCode = stationDetails.getStationCode();
        Optional<WrapperEntitiesOperations> opt = findById(stationCode);
        if (opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        WrapperEntities<StationDetails> wrapperEntities = (WrapperEntities) opt.get();
        WrapperEntity<StationDetails> wrapperEntity = new WrapperEntity<>(stationDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        wrapperEntities.getEntities().add(wrapperEntity);
        return repository.save(wrapperEntities);
    }

//    @Override
//    public WrapperEntitiesList findByStatusAndTypeAndBrokerCode( WrapperStatus status ,WrapperType wrapperType,String brokerCode, Integer page, Integer size) {
//        Pageable paging = PageRequest.of(page, size);
//        Page<WrapperEntitiesOperations> response = repository.findByStatusAndTypeAndBrokerCode(status, wrapperType, brokerCode,paging);
//        response.getTotalPages();
//        PageInfo pi = new PageInfo();
//        pi.setPage(page);
//        pi.setLimit(size);
//        pi.setItemsFound(response.getTotalPages());
//        WrapperEntitiesList wrapperEntitiesList = new WrapperEntitiesList();
//        response.getContent().forEach(WrapperEntitiesOperations::sortEntitesByCreatedAt);
//        wrapperEntitiesList.setWrapperEntities(response.getContent());
//        wrapperEntitiesList.setPageInfo(pi);
//        return wrapperEntitiesList;
//    }

    @Override
    public WrapperEntitiesList findByStatusAndTypeAndBrokerCodeAndIdLike(WrapperStatus status, WrapperType wrapperType, String brokerCode, String idLike, Integer page, Integer size, String sorting) {

        Sort sort;
        if ("DESC".equalsIgnoreCase(sorting)) {
            sort = Sort.by(Sort.Order.desc("id"));
        } else {
            sort = Sort.by(Sort.Order.asc("id"));
        }

        Pageable paging = PageRequest.of(page, size, sort);
        Page<WrapperEntitiesOperations<?>> response = null;

        if (status != null) {
            if (brokerCode != null && idLike != null) {
                response = repository.findByStatusAndTypeAndBrokerCodeAndIdLike(status, wrapperType, brokerCode, idLike, paging);
            } else if (brokerCode != null) {
                response = repository.findByStatusAndTypeAndBrokerCode(status, wrapperType, brokerCode, paging);
            } else if (idLike != null) {
                response = repository.findByStatusAndTypeAndIdLike(status, wrapperType, idLike, paging);
            } else {
                response = repository.findByStatusAndType(status, wrapperType, paging);
            }
        } else {
            if (brokerCode != null && idLike != null) {
                response = repository.findByTypeAndBrokerCodeAndIdLike(wrapperType, brokerCode, idLike, paging);
            } else if (brokerCode != null) {
                response = repository.findByTypeAndBrokerCode(wrapperType, brokerCode, paging);
            } else if (idLike != null) {
                response = repository.findByTypeAndIdLike(wrapperType, idLike, paging);
            } else {
                response = repository.findByType(wrapperType, paging);
            }
        }
       
        PageInfo pi = new PageInfo();
        pi.setPage(page);
        pi.setLimit(size);
        pi.setItemsFound(response.getTotalPages());
        WrapperEntitiesList wrapperEntitiesList = new WrapperEntitiesList();
        response.getContent().forEach(WrapperEntitiesOperations::sortEntitesByCreatedAt);
        wrapperEntitiesList.setWrapperEntities(response.getContent());
        wrapperEntitiesList.setPageInfo(pi);
        return wrapperEntitiesList;
    }

    @Override
    public Optional<WrapperEntitiesOperations> findById(String id) {
        return repository.findById(id).map(Function.identity());
    }

//    @Override
//    public List<WrapperEntitiesOperations> findAll() {
//        return new ArrayList<>(repository.findAll());
//    }
//
//
//    @Override
//        public Page<WrapperEntitiesOperations> findByStatusAndType(WrapperStatus status, WrapperType wrapperType) {
//            return repository.findByStatusAndType(status);
//        }
//
//    @Override
//    public List<WrapperEntitiesOperations> findByStatusNot(WrapperStatus status) {
//        return new ArrayList<WrapperEntitiesOperations>(repository.findByStatusNot(status));
//    }
//
//
//    @Override
//    public void updateWrapperEntitiesStatus(String id, WrapperStatus status) {
//        log.trace("updateWrapperEntitiesStatus start");
//        log.debug("updateWrapperEntitiesStatus id = {}, status = {}", id, status);
//        UpdateResult updateResult = mongoTemplate.updateFirst(
//                Query.query(Criteria.where(WrapperEntities.Fields.id).is(id)),
//                Update.update(WrapperEntities.Fields.status, status)
//                        .set(WrapperEntities.Fields.modifiedBy, auditorAware.getCurrentAuditor().orElse(null))
//                        .currentDate(WrapperEntities.Fields.modifiedAt),
//                WrapperEntities.class);
//        if (updateResult.getMatchedCount() == 0) {
//            throw new ResourceNotFoundException();
//        }
//        log.trace("updateWrapperEntitiesStatus end");
//    }

}
