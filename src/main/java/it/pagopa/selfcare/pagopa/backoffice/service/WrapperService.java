package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityChannel;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityChannels;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStation;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStations;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperChannelList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.WrapperStationList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperChannelsRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperStationsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.REGEX_GENERATE;
import static it.pagopa.selfcare.pagopa.backoffice.util.StringUtils.generator;

@Service
@Slf4j
public class WrapperService {

    private final ApiConfigClient apiConfigClient;

    private final WrapperRepository repository;

    private final WrapperStationsRepository wrapperStationsRepository;

    private final WrapperChannelsRepository wrapperChannelsRepository;

    private final AuditorAware<String> auditorAware;

    @Autowired
    public WrapperService(
            ApiConfigClient apiConfigClient,
            WrapperRepository repository,
            WrapperStationsRepository wrapperStationsRepository,
            WrapperChannelsRepository wrapperChannelsRepository,
            AuditorAware<String> auditorAware
    ) {
        this.apiConfigClient = apiConfigClient;
        this.repository = repository;
        this.wrapperStationsRepository = wrapperStationsRepository;
        this.wrapperChannelsRepository = wrapperChannelsRepository;
        this.auditorAware = auditorAware;
    }

    public static List<WrapperEntityOperations> getWrapperEntityOperationsSortedList(WrapperEntities wrapperEntities) {
        List<WrapperEntityOperations> list = new ArrayList<>(wrapperEntities.getEntities());
        list.sort(Comparator.comparing(WrapperEntityOperations::getCreatedAt, Comparator.reverseOrder()));
        return list;

    }

    public static List<WrapperEntityStation> getStationWrapperEntityOperationsSortedList(WrapperEntityStations wrapperEntities) {
        List<WrapperEntityStation> list = wrapperEntities.getEntities();
        list.sort(Comparator.comparing(WrapperEntityStation::getCreatedAt, Comparator.reverseOrder()));
        return list;
    }

    public static List<WrapperEntityChannel> getChannelWrapperEntityOperationsSortedList(WrapperEntityChannels wrapperEntities) {
        List<WrapperEntityChannel> list = wrapperEntities.getEntities();
        list.sort(Comparator.comparing(WrapperEntityChannel::getCreatedAt, Comparator.reverseOrder()));
        return list;
    }

    /**
     * Creates a new wrapper channel to be validated
     *
     * @param channelDetails the details of the new channel
     * @param status         the status of the new channel
     * @return the created wrapper channel
     */
    public WrapperEntities<ChannelDetails> createWrapperChannel(ChannelDetails channelDetails, WrapperStatus status) {
        String modifiedBy = this.auditorAware.getCurrentAuditor().orElse(null);

        WrapperEntity<ChannelDetails> wrapperEntity = new WrapperEntity<>(channelDetails);
        wrapperEntity.setStatus(status);
        WrapperEntities<ChannelDetails> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        wrapperEntities.setModifiedBy(modifiedBy);

        WrapperEntities<ChannelDetails> response;
        try {
            wrapperEntities.setCreatedBy(modifiedBy);
            response = this.repository.insert(wrapperEntities);
        } catch (DuplicateKeyException e) {
            response = update(channelDetails, null, status.name(), wrapperEntities.getCreatedBy());
        }
        return response;
    }

    /**
     * Creates a new wrapper station to be validated
     *
     * @param stationDetails the details of the new station
     * @param status         the status of the new station
     * @return the created wrapper station
     */
    public WrapperEntities<StationDetails> createWrapperStation(StationDetails stationDetails, WrapperStatus status) {
        String modifiedBy = this.auditorAware.getCurrentAuditor().orElse(null);

        WrapperEntity<StationDetails> wrapperEntity = new WrapperEntity<>(stationDetails);
        wrapperEntity.setStatus(status);
        WrapperEntities<StationDetails> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        wrapperEntities.setModifiedBy(modifiedBy);
        String createdBy = wrapperEntities.getCreatedBy();

        WrapperEntities<StationDetails> response;
        try {
            wrapperEntities.setCreatedBy(modifiedBy);
            response = this.repository.insert(wrapperEntities);
        } catch (DuplicateKeyException e) {
            response = update(stationDetails, null, status.name(), createdBy);
        }
        return response;
    }

    public WrapperEntities<ChannelDetails> update(
            ChannelDetails channelDetails,
            String note,
            String status,
            String createdBy
    ) {
        String channelCode = channelDetails.getChannelCode();
        Optional<WrapperEntities> optionalWrapperEntities = repository.findById(channelCode);

        if (optionalWrapperEntities.isEmpty()) {
            return createWrapperChannel(channelDetails, WrapperStatus.valueOf(status));
        }

        WrapperEntities<ChannelDetails> wrapperEntities = optionalWrapperEntities.get();
        wrapperEntities.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        WrapperEntity<ChannelDetails> wrapperEntity = new WrapperEntity<>(channelDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        wrapperEntity.setModifiedBy(auditorAware.getCurrentAuditor().orElse(null));
        wrapperEntities.setStatus(WrapperStatus.valueOf(status));
        wrapperEntities.getEntities().add(wrapperEntity);
        if (createdBy != null)
            wrapperEntities.setCreatedBy(createdBy);
        return repository.save(wrapperEntities);
    }

    /**
     * Updates a wrapper channel with the provided information
     *
     * @param channelCode    code of the channel to be updated
     * @param channelDetails details of the updated channel
     * @return the updated wrapper channel
     */
    public WrapperEntityChannels updateWrapperChannel(String channelCode, ChannelDetails channelDetails) {
        String modifiedBy = this.auditorAware.getCurrentAuditor().orElse(null);
        Instant now = Instant.now();

        Optional<WrapperEntityChannels> optionalWrapperEntities = this.wrapperChannelsRepository.findById(channelCode);

        if (optionalWrapperEntities.isEmpty()) {
            WrapperEntityChannel entityChannel = new WrapperEntityChannel(channelDetails);
            entityChannel.setCreatedAt(now);
            entityChannel.setModifiedAt(now);
            entityChannel.setModifiedBy(modifiedBy);
            entityChannel.setStatus(WrapperStatus.TO_CHECK_UPDATE);

            WrapperEntityChannels wrapperEntities = new WrapperEntityChannels(entityChannel);
            wrapperEntities.setStatus(WrapperStatus.TO_CHECK_UPDATE);
            wrapperEntities.setModifiedBy(modifiedBy);
            wrapperEntities.setCreatedBy(modifiedBy);
            wrapperEntities.setCreatedAt(now);
            return this.wrapperChannelsRepository.save(wrapperEntities);
        }

        WrapperEntityChannels wrapperEntities = optionalWrapperEntities.get();
        WrapperEntityChannel wrapper = getChannelWrapperEntityOperationsSortedList(wrapperEntities).get(0);
        WrapperStatus newWrapperStatus = getNewWrapperStatusForUpdate(channelCode, wrapper.getStatus());

        wrapperEntities.setModifiedBy(modifiedBy);
        wrapperEntities.setModifiedAt(now);
        wrapperEntities.setStatus(newWrapperStatus);

        WrapperEntityChannel wrapperEntity = new WrapperEntityChannel(channelDetails);
        wrapperEntity.setStatus(newWrapperStatus);
        wrapperEntity.setModifiedBy(modifiedBy);
        wrapperEntity.setModifiedAt(now);
        wrapperEntities.getEntities().add(wrapperEntity);
        return this.wrapperChannelsRepository.save(wrapperEntities);
    }

    /**
     * Updates a validated wrapper channel
     *
     * @param channelDetails the details of the channel
     * @param status         the status of the validated channel
     * @return the validated wrapper channel
     */
    public WrapperEntityChannels updateValidatedWrapperChannel(
            ChannelDetails channelDetails,
            WrapperStatus status
    ) {
        String channelCode = channelDetails.getChannelCode();
        WrapperEntityChannels wrapperEntities = this.wrapperChannelsRepository.findById(channelCode)
                .orElseThrow(() -> new AppException(AppError.WRAPPER_CHANNEL_NOT_FOUND, channelCode));
        String modifiedByOpt = this.auditorAware.getCurrentAuditor().orElse(null);

        wrapperEntities.setStatus(status);
        wrapperEntities.setModifiedAt(Instant.now());
        wrapperEntities.setModifiedByOpt(modifiedByOpt);


        WrapperEntityChannel entityChannel = getChannelWrapperEntityOperationsSortedList(wrapperEntities).get(0);
        entityChannel.setEntity(channelDetails);
        entityChannel.setModifiedAt(Instant.now());
        entityChannel.setModifiedByOpt(modifiedByOpt);
        entityChannel.setStatus(status);
        return this.wrapperChannelsRepository.save(wrapperEntities);
    }

    /**
     * Updates a validated wrapper station
     *
     * @param stationDetails the details of the station
     * @param status         the status of the validated station
     * @return the validated wrapper station
     */
    public WrapperEntityStations updateValidatedWrapperStation(StationDetails stationDetails, WrapperStatus status) {
        String stationCode = stationDetails.getStationCode();
        WrapperEntityStations wrapperEntities = this.wrapperStationsRepository.findById(stationCode)
                .orElseThrow(() -> new AppException(AppError.WRAPPER_STATION_NOT_FOUND, stationCode));
        String modifiedByOpt = this.auditorAware.getCurrentAuditor().orElse(null);

        wrapperEntities.setStatus(status);
        wrapperEntities.setModifiedAt(Instant.now());
        wrapperEntities.setModifiedByOpt(modifiedByOpt);

        WrapperEntityStation entityStation = getStationWrapperEntityOperationsSortedList(wrapperEntities).get(0);
        entityStation.setEntity(stationDetails);
        entityStation.setModifiedAt(Instant.now());
        entityStation.setModifiedByOpt(modifiedByOpt);
        entityStation.setStatus(status);
        return this.wrapperStationsRepository.save(wrapperEntities);
    }

    /**
     * Retrieve the wrapper station and updates the last entity with the operator review
     *
     * @param stationCode station code of the wrapper station to be updated
     * @param note        operator review note
     * @return the updated wrapper station
     */
    public WrapperEntityStations updateStationWithOperatorReview(String stationCode, String note) {
        WrapperEntityStations wrapperEntities = this.wrapperStationsRepository.findById(stationCode)
                .orElseThrow(() -> new AppException(AppError.WRAPPER_STATION_NOT_FOUND, stationCode));

        WrapperEntityStation entityStation = getStationWrapperEntityOperationsSortedList(wrapperEntities).get(0);
        WrapperStatus newWrapperStatus = getNewWrapperStatusForReview(stationCode, entityStation.getStatus());
        String modifiedByOpt = this.auditorAware.getCurrentAuditor().orElse(null);

        wrapperEntities.setStatus(newWrapperStatus);
        wrapperEntities.setModifiedAt(Instant.now());
        wrapperEntities.setModifiedByOpt(modifiedByOpt);

        entityStation.setNote(note);
        entityStation.setModifiedAt(Instant.now());
        entityStation.setModifiedByOpt(modifiedByOpt);
        entityStation.setStatus(newWrapperStatus);

        return this.wrapperStationsRepository.save(wrapperEntities);
    }

    public WrapperEntities<StationDetails> update(
            StationDetails stationDetails,
            String note,
            String status,
            String createdBy
    ) {
        String stationCode = stationDetails.getStationCode();
        Optional<WrapperEntities> optionalWrapperEntities = this.repository.findById(stationCode);

        if (optionalWrapperEntities.isEmpty()) {
            stationDetails.setActivationDate(Instant.now());
            return createWrapperStation(stationDetails, WrapperStatus.valueOf(status));
        }

        WrapperEntities<StationDetails> wrapperEntities = optionalWrapperEntities.get();
        WrapperEntity<StationDetails> wrapperEntity = new WrapperEntity<>(stationDetails);
        wrapperEntity.setNote(note);
        wrapperEntity.setStatus(WrapperStatus.valueOf(status));
        wrapperEntities.setStatus(WrapperStatus.valueOf(status));
        wrapperEntities.getEntities().add(wrapperEntity);
        if (createdBy != null)
            wrapperEntities.setCreatedBy(createdBy);
        return this.repository.save(wrapperEntities);
    }

    public WrapperEntitiesList findByStatusAndTypeAndBrokerCodeAndIdLike(
            WrapperStatus status,
            WrapperType wrapperType,
            String brokerCode,
            String idLike,
            Integer page,
            Integer size,
            String sorting
    ) {

        Sort sort;
        if ("DESC".equalsIgnoreCase(sorting)) {
            sort = Sort.by(Sort.Order.desc("id"));
        } else {
            sort = Sort.by(Sort.Order.asc("id"));
        }

        Pageable paging = PageRequest.of(page, size, sort);
        Page<WrapperEntities<?>> response = null;

        int switchCase = (brokerCode != null ? 1 : 0) | (idLike != null ? 2 : 0);
        if (status != null) {
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

    public WrapperEntityStations findStationById(String stationCode) {
        var response = this.wrapperStationsRepository.findById(stationCode)
                .orElseThrow(() -> new AppException(AppError.WRAPPER_STATION_NOT_FOUND, stationCode));
        response.sortEntitiesById();
        return response;
    }

    public WrapperEntityChannels findChannelById(String channelCode) {
        var response = this.wrapperChannelsRepository.findById(channelCode)
                .orElseThrow(() -> new AppException(AppError.WRAPPER_CHANNEL_NOT_FOUND, channelCode));
        response.sortEntitiesById();
        return response;
    }

    public Optional<WrapperEntityStations> findStationByIdOptional(String stationCode) {
        return this.wrapperStationsRepository.findById(stationCode);
    }

    public Optional<WrapperEntityChannels> findChannelByIdOptional(String channelCode) {
        return this.wrapperChannelsRepository.findById(channelCode);
    }

    /**
     * Retrieve a paginated list of wrapper channel filtered by broker's code and optionally by channel's code
     *
     * @param channelCode channel's code
     * @param brokerCode  broker's code
     * @param size        page size
     * @param page        page number
     * @return the paginated list
     */
    public WrapperChannelList getWrapperChannels(String channelCode, String brokerCode, Integer size, Integer page) {
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());

        Page<WrapperEntityChannels> response;
        if (channelCode == null) {
            response = this.wrapperChannelsRepository
                    .findByTypeAndBrokerCodeAndStatusNot(WrapperType.CHANNEL, brokerCode, WrapperStatus.APPROVED, paging);
        } else {
            response = this.wrapperChannelsRepository
                    .findByIdLikeAndTypeAndBrokerCodeAndStatusNot(channelCode, WrapperType.CHANNEL, brokerCode, WrapperStatus.APPROVED, paging);
        }

        return WrapperChannelList.builder()
                .wrapperEntities(response.getContent())
                .pageInfo(PageInfo.builder()
                        .page(page)
                        .limit(size)
                        .totalItems(response.getTotalElements())
                        .totalPages(response.getTotalPages())
                        .itemsFound(response.getNumberOfElements())
                        .build())
                .build();
    }

    /**
     * Retrieve a paginated list of wrapper station filtered by broker's code and optionally by station's code
     *
     * @param stationCode station's code
     * @param brokerCode  broker's code
     * @param size        page size
     * @param page        page number
     * @return the paginated list
     */
    public WrapperStationList getWrapperStations(String stationCode, String brokerCode, Integer size, Integer page) {
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());

        Page<WrapperEntityStations> response;
        if (stationCode == null) {
            response = this.wrapperStationsRepository
                    .findByTypeAndBrokerCodeAndStatusNot(WrapperType.STATION, brokerCode, WrapperStatus.APPROVED, paging);
        } else {
            response = this.wrapperStationsRepository
                    .findByIdLikeAndTypeAndBrokerCodeAndStatusNot(stationCode, WrapperType.STATION, brokerCode, WrapperStatus.APPROVED, paging);
        }

        return WrapperStationList.builder()
                .wrapperEntities(response.getContent())
                .pageInfo(PageInfo.builder()
                        .page(page)
                        .limit(size)
                        .totalItems(response.getTotalElements())
                        .totalPages(response.getTotalPages())
                        .itemsFound(response.getNumberOfElements())
                        .build())
                .build();
    }

    /**
     * Compute the first available station/channel code.
     * <p>
     * Retrieves all station and channel from Api-Config and Wrapper collection and process the list by extracting the first
     * valid code.
     * @param taxCode tax code
     * @return the first valid code
     */
    public String getFirstValidCodeV2(String taxCode) {
        List<String> usedCodes = new LinkedList<>();
        try {
            Stations stations = this.apiConfigClient.getStations(100, 0, Sort.Direction.DESC.name(), taxCode, null, null);
            WrapperStationList wrapperStations = findStationByIdLikeOrTypeOrBrokerCode(taxCode, 0, 100);
            usedCodes.addAll(stations.getStationsList().parallelStream().map(Station::getStationCode).toList());
            usedCodes.addAll(wrapperStations.getWrapperEntities().parallelStream().map(WrapperEntityStations::getId).toList());
        } catch (FeignException.NotFound e) {
            log.warn("The provided tax code is not of a CI broker", e);
        }

        Channels channels = this.apiConfigClient.getChannels(null, taxCode, Sort.Direction.DESC.name(), 100, 0);
        WrapperChannelList wrapperChannels = findByIdLikeOrTypeOrBrokerCode(taxCode, 0, 100);

        usedCodes.addAll(channels.getChannelList().parallelStream().map(Channel::getChannelCode).toList());
        usedCodes.addAll(wrapperChannels.getWrapperEntities().parallelStream().map(WrapperEntityChannels::getId).toList());

        Set<String> validCodes = usedCodes.parallelStream()
                .filter(s -> s.matches(REGEX_GENERATE))
                .collect(Collectors.toSet());
        return generator(validCodes, taxCode);
    }

    /**
     * Updates a wrapper station with the provided information if exist otherwise inserts it
     *
     * @param stationCode    code of the station to be updated
     * @param stationDetails details of the updated station
     * @return the updated wrapper station
     */
    public WrapperEntityStations updateWrapperStation(String stationCode, StationDetails stationDetails) {
        Optional<WrapperEntityStations> optionalWrapperEntities = this.wrapperStationsRepository.findById(stationCode);
        String modifiedBy = this.auditorAware.getCurrentAuditor().orElse(null);
        Instant now = Instant.now();

        if (optionalWrapperEntities.isEmpty()) {
            WrapperEntityStation entityStation = new WrapperEntityStation(stationDetails);
            entityStation.setCreatedAt(now);
            entityStation.setModifiedAt(now);
            entityStation.setModifiedBy(modifiedBy);
            entityStation.setStatus(WrapperStatus.TO_CHECK_UPDATE);

            WrapperEntityStations wrapperEntities = new WrapperEntityStations(entityStation);
            wrapperEntities.setStatus(WrapperStatus.TO_CHECK_UPDATE);
            wrapperEntities.setModifiedBy(modifiedBy);
            wrapperEntities.setCreatedBy(modifiedBy);
            wrapperEntities.setCreatedAt(now);
            return this.wrapperStationsRepository.save(wrapperEntities);
        }

        WrapperEntityStations wrapperEntities = optionalWrapperEntities.get();
        WrapperEntityStation mostRecentEntity = getStationWrapperEntityOperationsSortedList(wrapperEntities).get(0);
        WrapperStatus newWrapperStatus = getNewWrapperStatusForUpdate(stationCode, mostRecentEntity.getStatus());

        WrapperEntityStation entityStation = new WrapperEntityStation(stationDetails);
        entityStation.setModifiedAt(now);
        entityStation.setModifiedBy(modifiedBy);
        entityStation.setStatus(newWrapperStatus);

        wrapperEntities.setStatus(newWrapperStatus);
        wrapperEntities.setModifiedAt(now);
        wrapperEntities.setModifiedBy(modifiedBy);
        wrapperEntities.getEntities().add(entityStation);
        return this.wrapperStationsRepository.save(wrapperEntities);
    }

    /**
     * Updates the wrapper channel with the operator review
     *
     * @param channelCode code of the channel to be updated
     * @param note        operator's note
     * @return the updated wrapper channel
     */
    public WrapperEntityChannels updateChannelWithOperatorReview(String channelCode, String note) {
        WrapperEntityChannels wrapperEntities = this.wrapperChannelsRepository.findById(channelCode)
                .orElseThrow(() -> new AppException(AppError.WRAPPER_CHANNEL_NOT_FOUND, channelCode));

        WrapperEntityChannel wrapper = getChannelWrapperEntityOperationsSortedList(wrapperEntities).get(0);
        WrapperStatus newWrapperStatus = getNewWrapperStatusForReview(channelCode, wrapper.getStatus());
        String modifiedByOpt = this.auditorAware.getCurrentAuditor().orElse(null);

        wrapperEntities.setStatus(newWrapperStatus);
        wrapperEntities.setModifiedAt(Instant.now());
        wrapperEntities.setModifiedByOpt(modifiedByOpt);

        wrapper.setNote(note);
        wrapper.setModifiedAt(Instant.now());
        wrapper.setModifiedByOpt(modifiedByOpt);
        wrapper.setStatus(newWrapperStatus);

        return this.wrapperChannelsRepository.save(wrapperEntities);
    }

    private WrapperStatus getNewWrapperStatusForReview(String stationCode, WrapperStatus oldWrapperStatus) {
        WrapperStatus newWrapperStatus;
        if (oldWrapperStatus.equals(WrapperStatus.TO_CHECK)) {
            newWrapperStatus = WrapperStatus.TO_FIX;
        } else if (oldWrapperStatus.equals(WrapperStatus.TO_CHECK_UPDATE)) {
            newWrapperStatus = WrapperStatus.TO_FIX_UPDATE;
        } else {
            throw new AppException(AppError.WRAPPER_STATION_INVALID_STATUS, stationCode);
        }
        return newWrapperStatus;
    }

    private WrapperStatus getNewWrapperStatusForUpdate(String stationCode, WrapperStatus oldWrapperStatus) {
        WrapperStatus newWrapperStatus;
        if (oldWrapperStatus.equals(WrapperStatus.TO_CHECK) || oldWrapperStatus.equals(WrapperStatus.TO_FIX)) {
            newWrapperStatus = WrapperStatus.TO_CHECK;
        } else if (
                oldWrapperStatus.equals(WrapperStatus.APPROVED)
                        || oldWrapperStatus.equals(WrapperStatus.TO_CHECK_UPDATE)
                        || oldWrapperStatus.equals(WrapperStatus.TO_FIX_UPDATE))
        {
            newWrapperStatus = WrapperStatus.TO_CHECK_UPDATE;
        } else {
            throw new AppException(AppError.WRAPPER_STATION_INVALID_STATUS, stationCode);
        }
        return newWrapperStatus;
    }

    private WrapperChannelList findByIdLikeOrTypeOrBrokerCode(
            String brokerCode,
            Integer page,
            Integer size
    ) {
        Pageable paging = PageRequest.of(page, size);
        Page<WrapperEntityChannels> response =
                this.wrapperChannelsRepository.findByTypeAndBrokerCode(WrapperType.CHANNEL, brokerCode, paging);

        return WrapperChannelList.builder()
                .wrapperEntities(response.getContent())
                .pageInfo(PageInfo.builder()
                        .page(page)
                        .limit(size)
                        .totalItems(response.getTotalElements())
                        .totalPages(response.getTotalPages())
                        .itemsFound(response.getNumberOfElements())
                        .build())
                .build();
    }

    private WrapperStationList findStationByIdLikeOrTypeOrBrokerCode(
            String brokerCode,
            Integer page,
            Integer size
    ) {
        Pageable paging = PageRequest.of(page, size);
        Page<WrapperEntityStations> response =
                this.wrapperStationsRepository.findByTypeAndBrokerCode(WrapperType.STATION, brokerCode, paging);

        return WrapperStationList.builder()
                .wrapperEntities(response.getContent())
                .pageInfo(PageInfo.builder()
                        .page(page)
                        .limit(size)
                        .totalItems(response.getTotalElements())
                        .totalPages(response.getTotalPages())
                        .itemsFound(response.getNumberOfElements())
                        .build())
                .build();
    }
}
