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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
public class WrapperConnectorImpl implements WrapperConnector {

    private final WrapperRepository repository;
    private final AuditorAware<String> auditorAware;


    @Autowired
    public WrapperConnectorImpl(WrapperRepository repository, AuditorAware<String> auditorAware) {
        this.repository = repository;
        this.auditorAware = auditorAware;
    }


    @Override
    public WrapperEntities<ChannelDetails> insert(ChannelDetails channelDetails, String note, String status) {
        WrapperEntity<ChannelDetails> wrapperEntity = new WrapperEntity<>(channelDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        WrapperEntities<ChannelDetails> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        wrapperEntities.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        WrapperEntities<ChannelDetails> response = null;
        try {
            response = repository.insert(wrapperEntities);
        } catch (DuplicateKeyException e) {
            response = (WrapperEntities<ChannelDetails>) update(channelDetails, note, status);
        }
        return response;
    }

    @Override
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
            response = (WrapperEntities<StationDetails>) update(stationDetails, note, status, createdBy);
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
        WrapperEntity<StationDetails> wrapperEntity = new WrapperEntity<>(stationDetails);
        wrapperEntity.setModifiedByOpt(modifiedByOpt);
        wrapperEntities.updateCurrentWrapperEntity(wrapperEntity, status, note, modifiedByOpt);
        return repository.save(wrapperEntities);
    }

    @Override
    public WrapperEntitiesOperations<StationDetails> update(StationDetails stationDetails, String note, String status, String createdBy) {
        String stationCode = stationDetails.getStationCode();
        Optional<WrapperEntitiesOperations> opt = findById(stationCode);
        if (opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        WrapperEntities<StationDetails> wrapperEntities = (WrapperEntities) opt.get();
        WrapperEntity<StationDetails> wrapperEntity = new WrapperEntity<>(stationDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        wrapperEntities.setStatus(WrapperStatus.valueOf(status));
        wrapperEntities.getEntities().add(wrapperEntity);
        if (createdBy != null)
            wrapperEntities.setCreatedBy(createdBy);
        return repository.save(wrapperEntities);
    }

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
            switch ((brokerCode != null ? 1 : 0) | (idLike != null ? 2 : 0)) {
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
            switch ((brokerCode != null ? 1 : 0) | (idLike != null ? 2 : 0)) {
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
       
        PageInfo pi = new PageInfo();
        pi.setPage(page);
        pi.setLimit(size);
        pi.setItemsFound(response.getTotalPages());
        WrapperEntitiesList wrapperEntitiesList = new WrapperEntitiesList();
        wrapperEntitiesList.setWrapperEntities(response.getContent());
        wrapperEntitiesList.setPageInfo(pi);
        return wrapperEntitiesList;
    }

    @Override
    public Optional<WrapperEntitiesOperations> findById(String id) {
        return repository.findById(id).map(Function.identity());
    }

    @Override
    public WrapperEntitiesList findByIdLikeOrTypeOrBrokerCode(String idLike, WrapperType wrapperType,String brokerCode, Integer page, Integer size) {

        Pageable paging = PageRequest.of(page, size);
        Page<WrapperEntitiesOperations<?>> response;

        if (brokerCode == null && idLike == null) {
            response = repository.findByType(wrapperType, paging);
        } else if (brokerCode == null) {
            response = repository.findByIdLikeAndType(idLike, wrapperType, paging);
        } else if (idLike == null) {
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
