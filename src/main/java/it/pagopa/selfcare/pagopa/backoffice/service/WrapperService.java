package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.REGEX_GENERATE;
import static it.pagopa.selfcare.pagopa.backoffice.util.StringUtils.generator;

@Service
public class WrapperService {

    @Autowired
    private ApiConfigClient apiConfigClient;

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
            throw new AppException(AppError.WRAPPER_CHANNEL_NOT_FOUND, channelCode);
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
            throw new AppException(AppError.WRAPPER_CHANNEL_NOT_FOUND, channelCode);
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
            throw new AppException(AppError.WRAPPER_STATION_NOT_FOUND, stationCode);
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
            throw new AppException(AppError.WRAPPER_STATION_NOT_FOUND, stationCode);
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
                .orElseThrow(() -> new AppException(AppError.WRAPPER_NOT_FOUND, id));
        response.sortEntitiesById();
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

    public String getFirstValidStationCodeV2(String taxCode) {
        Stations stations = apiConfigClient.getStations(100, 0, "DESC", null, null, taxCode);
        WrapperEntitiesList stationMongoList = findByIdLikeOrTypeOrBrokerCode(taxCode, WrapperType.STATION, null, 0, 100);

        List<String> stationCodes = new LinkedList<>();
        stationCodes.addAll(stationMongoList.getWrapperEntities().stream().map(WrapperEntities::getId).toList());
        stationCodes.addAll(stations.getStationsList().stream().map(Station::getStationCode).toList());

        Set<String> validCodes = stationCodes.stream()
                .filter(s -> s.matches(REGEX_GENERATE))
                .collect(Collectors.toSet());
        return generator(validCodes, taxCode);
    }
    public String getFirstValidChannelCodeV2(String taxCode) {
        Channels channels = apiConfigClient.getChannels(100, 0, null, taxCode, "DESC");
        WrapperEntitiesList channelMongoList = findByIdLikeOrTypeOrBrokerCode(null, WrapperType.CHANNEL, taxCode, 0, 100);

        List<String> channelCodes = new LinkedList<>();
        channelCodes.addAll(channelMongoList.getWrapperEntities().stream().map(WrapperEntities::getId).toList());
        channelCodes.addAll(channels.getChannelList().stream().map(Channel::getChannelCode).toList());

        Set<String> validCodes = channelCodes.stream()
                .filter(s -> s.matches(REGEX_GENERATE))
                .collect(Collectors.toSet());
        return generator(validCodes, taxCode);
    }

    /**
     * @param details  station details
     * @param note     not used now
     * @param status   @link{WrapperStatus}
     * @param createBy creator
     * @return it inserts the station if it doesn't exist or updates it
     */
    public WrapperEntities<StationDetails> upsert(StationDetails details, String note, String status, String createBy) {
        var entity = repository.findById(details.getStationCode());
        if(entity.isPresent()) {
            return update(details, note, status, createBy);
        } else {
            details.setActivationDate(null);

            WrapperEntity<StationDetails> wrapperEntity = new WrapperEntity<>(details);
            wrapperEntity.setNote(note);
            wrapperEntity.setStatus(WrapperStatus.TO_CHECK_UPDATE);
            wrapperEntity.setCreatedAt(Instant.now());
            wrapperEntity.setModifiedAt(Instant.now());
            wrapperEntity.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));

            WrapperEntities<StationDetails> wrapperEntities = new WrapperEntities<>(wrapperEntity);
            wrapperEntities.setCreatedAt(null);
            wrapperEntities.setCreatedBy(null);
            wrapperEntities.setModifiedAt(Instant.now());
            wrapperEntities.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));

            return repository.insert(wrapperEntities);
        }
    }


}
