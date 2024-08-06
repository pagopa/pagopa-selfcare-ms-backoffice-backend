package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.MaintenanceHoursSummaryResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceListResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceListState;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.UpdateStationMaintenance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class StationMaintenanceService {

    private final ApiConfigClient apiConfigClient;

    @Autowired
    public StationMaintenanceService(ApiConfigClient apiConfigClient) {
        this.apiConfigClient = apiConfigClient;
    }

    /**
     * Retrieves the list of station's maintenance of the specified broker that match the provided filters
     *
     * @param brokerCode broker's tax code
     * @param stationCode station's code, used to filter out results
     * @param state state of the maintenance (based on start and end date), used to filter out results
     * @param year year of the maintenance, used to filter out results
     * @param limit size of the requested page
     * @param page page number
     * @return the filtered list of station's maintenance
     */
    public StationMaintenanceListResource getStationMaintenances(
            String brokerCode,
            String stationCode,
            StationMaintenanceListState state,
            Integer year,
            Integer limit,
            Integer page
    ) {
        OffsetDateTime startDateTimeBefore = null;
        OffsetDateTime startDateTimeAfter = null;
        OffsetDateTime endDateTimeBefore = null;
        OffsetDateTime endDateTimeAfter = null;

        if (state != null) {
            if (state.equals(StationMaintenanceListState.FINISHED)) {
                endDateTimeBefore = getDateToday();
            }
            if (state.equals(StationMaintenanceListState.SCHEDULED_AND_IN_PROGRESS)) {
                endDateTimeAfter = getDateToday();
            }
            if (state.equals(StationMaintenanceListState.SCHEDULED)) {
                startDateTimeAfter = getDateToday();
            }
            if (state.equals(StationMaintenanceListState.IN_PROGRESS)) {
                startDateTimeBefore = getDateToday();
                endDateTimeAfter = getDateToday();
            }
        }

        if (year != null
        ) {
            startDateTimeBefore = startDateTimeBefore != null ? startDateTimeBefore.withYear(year) : getEndOfYear(year);
            startDateTimeAfter = startDateTimeAfter != null ? startDateTimeAfter.withYear(year) : getStartOfYear(year);
        }

        return this.apiConfigClient.getStationMaintenances(
                brokerCode,
                stationCode,
                startDateTimeBefore,
                startDateTimeAfter,
                endDateTimeBefore,
                endDateTimeAfter,
                limit,
                page
        );
    }

    /**
     * Creates a new station maintenance for the specified broker with the provided details
     *
     * @param brokerCode broker's tax code
     * @param createStationMaintenance detail of the new station's maintenance
     * @return the details of the created maintenance
     */
    public StationMaintenanceResource createStationMaintenance(
            String brokerCode,
            CreateStationMaintenance createStationMaintenance
    ) {
        return this.apiConfigClient.createStationMaintenance(brokerCode, createStationMaintenance);
    }

    /**
     * Updates the station's maintenance with the specified broker tax code and maintenance id with the provided new
     * details. If the maintenance is in progress only end date time can be updated otherwise start date time and standIn
     * flag can be updated too.
     *
     * @param brokerCode    broker's tax code
     * @param maintenanceId station maintenance id
     * @param updateStationMaintenance details to be updated
     * @return the details of the updated maintenance
     */
    public StationMaintenanceResource updateStationMaintenance(
            String brokerCode,
            Long maintenanceId,
            UpdateStationMaintenance updateStationMaintenance
    ) {
        return this.apiConfigClient.updateStationMaintenance(brokerCode, maintenanceId, updateStationMaintenance);
    }

    /**
     * Retrieves broker related station maintenance summary for the provided year
     *
     * @param brokerCode      broker id to use for summary retrieval
     * @param maintenanceYear year in format yyyy, to be used for summary retrieval
     * @return maintenance summary for the provided year and brokerCode
     */
    public MaintenanceHoursSummaryResource getBrokerMaintenancesSummary(String brokerCode, String maintenanceYear) {
        return this.apiConfigClient.getBrokerMaintenancesSummary(brokerCode, maintenanceYear);
    }

    /**
     * Recovers a station maintenance, given its brokerCode and maintenanceId.
     * If the provided brokerCode doesn't match the one related to the persisted one for the given maintenance,
     * it will throw the maintenance not found exception
     *
     * @param brokerCode    brokerCode to be used as filter in the maintenance recovery
     * @param maintenanceId station maintenance id to be used for the detail recovery
     * @return station maintenance data, provided in an instance of StationMaintenanceResource
     */
    public StationMaintenanceResource getStationMaintenance(String brokerCode, Long maintenanceId) {
        return this.apiConfigClient.getStationMaintenance(brokerCode, maintenanceId);
    }

    /**
     * Delete the station's maintenance with the provided maintenance id and broker code
     *
     * @param brokerCode    broker's tax code
     * @param maintenanceId station maintenance id
     */
    public void deleteStationMaintenance(
            String brokerCode,
            Long maintenanceId
    ) {
        this.apiConfigClient.deleteStationMaintenance(brokerCode, maintenanceId);
    }

    /**
     * Terminate the station's maintenance with the specified id.
     * Update the endDateTime field with the current timestamp rounded to the next 15 minutes.
     *
     * @param brokerCode    broker's tax code
     * @param maintenanceId maintenance's id
     */
    public void finishStationMaintenance(String brokerCode, Long maintenanceId) {
        StationMaintenanceResource maintenance = this.apiConfigClient.getStationMaintenance(brokerCode, maintenanceId);

        OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        if (!(maintenance.getStartDateTime().isBefore(now) && maintenance.getEndDateTime().isAfter(now))) {
            throw new AppException(AppError.STATION_MAINTENANCE_NOT_IN_PROGRESS);
        }

        long mod = now.getMinute() % 15;
        UpdateStationMaintenance update = UpdateStationMaintenance.builder()
                .endDateTime(now.plusMinutes(15 - mod))
                .build();

        this.apiConfigClient.updateStationMaintenance(brokerCode, maintenanceId, update);
    }

    private OffsetDateTime getDateToday() {
        return OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }

    private OffsetDateTime getStartOfYear(int year) {
        return getDateToday().withYear(year).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0);
    }

    private OffsetDateTime getEndOfYear(int year) {
        return getDateToday().withYear(year).withMonth(12).withDayOfMonth(31).withHour(23).withMinute(59);
    }
}
