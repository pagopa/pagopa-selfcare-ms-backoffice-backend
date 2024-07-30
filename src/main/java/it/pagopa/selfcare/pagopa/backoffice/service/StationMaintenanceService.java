package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
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

    public StationMaintenanceResource createStationMaintenance(
            String brokerCode,
            CreateStationMaintenance createStationMaintenance
    ) {
        return this.apiConfigClient.createStationMaintenance(brokerCode, createStationMaintenance);
    }

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
     * @param maintenanceYear year in format yyyy, to be used for summary retreival
     * @return maintenance summary for the provided year and brokerCode
     */
    public MaintenanceHoursSummaryResource getBrokerMaintenancesSummary(String brokerCode, String maintenanceYear) {
        return this.apiConfigClient.getBrokerMaintenancesSummary(brokerCode, maintenanceYear);
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
