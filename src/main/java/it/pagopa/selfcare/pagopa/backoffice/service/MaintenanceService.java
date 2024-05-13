package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.maintenance.MaintenanceMessage;
import it.pagopa.selfcare.pagopa.backoffice.repository.MaintenanceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public MaintenanceService(MaintenanceRepository maintenanceRepository, ModelMapper modelMapper) {
        this.maintenanceRepository = maintenanceRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieve the maintenance message from MongoDB
     *
     * @return the maintenance message
     */
    public MaintenanceMessage getMaintenanceMessages() {
        return this.maintenanceRepository.findAll().stream()
                .map(entity -> this.modelMapper.map(entity, MaintenanceMessage.class))
                .findFirst()
                .orElseThrow(() -> new AppException(AppError.MAINTENANCE_MESSAGES_NOT_FOUND));
    }
}
