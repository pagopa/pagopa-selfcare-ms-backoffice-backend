package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import com.mongodb.client.result.UpdateResult;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.WrapperConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.time.Instant;
import java.time.LocalDateTime;
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
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<Object>(channelDetails);
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
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<Object>(stationDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        WrapperEntities wrapperEntities = new WrapperEntities(wrapperEntity);
        wrapperEntities.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        WrapperEntities response = null;
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
        WrapperEntities wrapperEntities = (WrapperEntities) opt.get();
        wrapperEntities.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        WrapperEntity wrapperEntity = new WrapperEntity<>(channelDetails);
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
        WrapperEntities wrapperEntities = (WrapperEntities) opt.get();
        String modifiedByOpt = auditorAware.getCurrentAuditor().orElse(null);
        wrapperEntities.updateCurrentWrapperEntity(new WrapperEntity(channelDetails), status, note, modifiedByOpt);
        return repository.save(wrapperEntities);
    }

    @Override
    public WrapperEntitiesOperations<StationDetails> updateByOpt(StationDetails stationDetails, String note, String status) {
        String stationCode = stationDetails.getStationCode();
        Optional<WrapperEntitiesOperations> opt = findById(stationCode);
        if (opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        WrapperEntities wrapperEntities = (WrapperEntities) opt.get();
        String modifiedByOpt = auditorAware.getCurrentAuditor().orElse(null);
        WrapperEntity wrapperEntity = new WrapperEntity(stationDetails);
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
        WrapperEntities wrapperEntities = (WrapperEntities) opt.get();
        WrapperEntity wrapperEntity = new WrapperEntity<>(stationDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        wrapperEntities.getEntities().add(wrapperEntity);
        return repository.save(wrapperEntities);
    }

    @Override
    public Optional<WrapperEntitiesOperations> findById(String id) {
        return repository.findById(id).map(Function.identity());
    }

//    @Override
//    public WrapperEntitiesOperations save(WrapperEntitiesOperations wrapperEntitiesOperations) {
//        WrapperEntities wrapperEntities = (WrapperEntities) wrapperEntitiesOperations;
//        //wrapperEntities.setNew(false);
//        if (wrapperEntities.getCreatedAt() == null) {
//            wrapperEntities.setCreatedAt(Instant.now());
//        }
//        if (wrapperEntities.getCreatedBy() == null) {
//            wrapperEntities.setCreatedBy(auditorAware.getCurrentAuditor().orElse(null));
//        }
//        return repository.save(wrapperEntities);
//    }
//
//
//    @Override
//    public Optional<WrapperEntitiesOperations> findById(String id) {
//        return repository.findById(id).map(Function.identity());
//    }
//
//    @Override
//    public boolean existsById(String id) {
//        return repository.existsById(id);
//    }
//
//
//    @Override
//    public List<WrapperEntitiesOperations> findAll() {
//        return new ArrayList<>(repository.findAll());
//    }
//
//
//    @Override
//        public List<WrapperEntitiesOperations> findByStatus(WrapperStatus status) {
//            return new ArrayList<WrapperEntitiesOperations>(repository.findByStatus(status));
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
