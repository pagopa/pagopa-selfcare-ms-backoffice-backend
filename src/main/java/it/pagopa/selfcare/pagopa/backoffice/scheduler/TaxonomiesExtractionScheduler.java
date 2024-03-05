package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.client.TaxonomyClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.TaxonomyEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.TaxonomyGroupAreaEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.TaxonomyGroupEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.client.TaxonomyDTO;
import it.pagopa.selfcare.pagopa.backoffice.repository.TaxonomyGroupRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.TaxonomyRepository;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static it.pagopa.selfcare.pagopa.backoffice.config.LoggingAspect.*;

/**
 * Contains the scheduled function to be used for taxonomy extraction from the external source using the API Client,
 * saving the result on the taxonomies and taxonomy-groups cosmos account
 */
@Component
@Slf4j
public class TaxonomiesExtractionScheduler {

    private final TaxonomyRepository taxonomyRepository;

    private final TaxonomyGroupRepository taxonomyGroupRepository;

    private final TaxonomyClient taxonomyClient;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ITALIAN);



    public TaxonomiesExtractionScheduler(
            TaxonomyRepository taxonomyRepository,
            TaxonomyGroupRepository taxonomyGroupRepository,
            TaxonomyClient taxonomyClient) {
        this.taxonomyRepository = taxonomyRepository;
        this.taxonomyGroupRepository = taxonomyGroupRepository;
        this.taxonomyClient = taxonomyClient;
    }

    /**
     * Method containing the scheduled function
     */
    @Scheduled(cron = "${cron.job.schedule.expression.taxonomies-extraction}")
    @SchedulerLock(name = "taxonomiesExtraction", lockAtMostFor = "180m", lockAtLeastFor = "15m")
    @Async
    @Transactional
    public void extractTaxinomies() {

        updateMDCForStartExecution();
        log.info("[Extract-Taxonomies] extraction starting");
        try {

            List<TaxonomyDTO> taxonomyDTOList = taxonomyClient.getTaxonomies();

            Map<String, TaxonomyGroupEntity> taxonomyGroupsMap = new HashMap<>();
            List<TaxonomyEntity> taxonomyEntities = new ArrayList<>();

            taxonomyDTOList.forEach(taxonomyDTO -> {
                TaxonomyEntity taxonomyEntity = new TaxonomyEntity();
                BeanUtils.copyProperties(taxonomyDTO, taxonomyEntity);
                taxonomyEntity.setStartDate(LocalDate.parse(taxonomyDTO.getStartDate(), dateTimeFormatter)
                        .atStartOfDay().toInstant(ZoneOffset.UTC));
                taxonomyEntity.setEndDate(LocalDate.parse(taxonomyDTO.getEndDate(), dateTimeFormatter)
                        .atTime(23,59,59,999).toInstant(ZoneOffset.UTC));
                taxonomyEntities.add(taxonomyEntity);

                if (!taxonomyGroupsMap.containsKey(taxonomyDTO.getEcType())) {
                    TaxonomyGroupEntity taxonomyGroupEntities = new TaxonomyGroupEntity();
                    taxonomyGroupEntities.setAreas(new HashSet<>());
                    taxonomyGroupEntities.setEcType(taxonomyDTO.getEcType());
                    taxonomyGroupEntities.setEcTypeCode(taxonomyDTO.getEcTypeCode());
                    taxonomyGroupsMap.put(taxonomyDTO.getEcType(), taxonomyGroupEntities);
                }
                TaxonomyGroupEntity taxonomyGroupEntities = taxonomyGroupsMap.get(taxonomyDTO.getEcType());
                taxonomyGroupEntities.getAreas().add(
                        TaxonomyGroupAreaEntity
                        .builder()
                                .macroAreaDescription(taxonomyDTO.getMacroAreaDescription())
                                .macroAreaEcProgressive(taxonomyDTO.getMacroAreaEcProgressive())
                                .macroAreaName(taxonomyDTO.getMacroAreaName())
                        .build()
                );

            });

            taxonomyRepository.deleteAll();
            taxonomyRepository.saveAll(taxonomyEntities);
            taxonomyGroupRepository.deleteAll();
            taxonomyGroupRepository.saveAll(new ArrayList<>(taxonomyGroupsMap.values()));

            updateMDCForEndExecution();
            log.info("[Extract-Taxonomies] extraction complete!");

        } catch (Exception e) {
            updateMDCError(e);
            log.error("[Extract-Taxonomies] an error occurred during the taxonomy extraction", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    private void updateMDCForStartExecution() {
        MDC.put(METHOD, "taxonomiesExtraction");
        MDC.put(START_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
        MDC.put(OPERATION_ID, UUID.randomUUID().toString());
        MDC.put(ARGS, "");
    }


    private void updateMDCForEndExecution() {
        MDC.put(STATUS, "OK");
        MDC.put(CODE, "201");
        MDC.put(RESPONSE_TIME, getExecutionTime());
    }

    private void updateMDCError(Exception e) {
        MDC.put(STATUS, "KO");
        MDC.put(CODE, "500");
        MDC.put(RESPONSE_TIME, getExecutionTime());
        MDC.put(FAULT_CODE, "Extract Taxonomies");
        MDC.put(FAULT_DETAIL, e.getMessage());
    }


}
