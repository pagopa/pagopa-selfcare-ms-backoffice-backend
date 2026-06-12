package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CreditorInstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.*;

import static it.pagopa.selfcare.pagopa.backoffice.util.MailTextConstants.*;

@Service
public class AsyncNotificationService {
    private static final String IBAN = "iban";
    private static final String CI_NAME = "ciName";
    private static final String ENVIRONMENT = "environment";

    private final String environment;
    private final AwsSesClient awsSesClient;
    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    public AsyncNotificationService(
            @Value("${info.properties.environment}") String environment,
            AwsSesClient awsSesClient,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient
    ) {
        this.environment = environment;
        this.awsSesClient = awsSesClient;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
    }

    /**
     * Notify all creditor institutions that have an active subscription or a request/offer to the
     * specified bundle
     *
     * @param ciTaxCodes  set of tax codes to be notified
     * @param bundleName bundle name
     * @param pspName    payment service provider name
     */
    @Async
    public void notifyDeletePSPBundleAsync(
            Set<String> ciTaxCodes,
            String bundleName,
            String pspName
    ) {
        Context bodyContext = buildBundleEmailHtmlBodyContext(bundleName, pspName);
        ciTaxCodes.parallelStream().forEach(
                ciTaxCode -> {
                    EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                            .institutionTaxCode(ciTaxCode)
                            .subject(BUNDLE_DELETE_SUBJECT)
                            .textBody(String.format(BUNDLE_DELETE_BODY, pspName, bundleName, getEnvParam()))
                            .htmlBodyFileName("deleteBundleEmail.html")
                            .htmlBodyContext(bodyContext)
                            .destinationUserType(SelfcareProductUser.ADMIN)
                            .build();
                    this.awsSesClient.sendEmail(messageDetail);
                }
        );
    }

    @Async
    public void notifyIbanCreation(String ciTaxCode) {
        String ciName = getCiName(ciTaxCode);
        Context bodyContext = buildHtmlBodyContext(
                List.of(
                        Pair.of(ENVIRONMENT, getEnvParam()),
                        Pair.of(CI_NAME, ciName)));
        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(IBAN_CREATE_SUBJECT)
                .textBody(String.format(IBAN_CREATE_BODY, ciName, getEnvParam()))
                .htmlBodyFileName("createIbanNotificationEmail.html")
                .htmlBodyContext(bodyContext)
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();
        this.awsSesClient.sendEmail(messageDetail);
    }

    @Async
    public void notifyIbanUpdate(String ciTaxCode, String iban) {
        String ciName = getCiName(ciTaxCode);
        Context bodyContext = buildHtmlBodyContext(
                List.of(
                        Pair.of(ENVIRONMENT, getEnvParam()),
                        Pair.of(CI_NAME, ciName),
                        Pair.of(IBAN, iban)));
        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(IBAN_UPDATE_SUBJECT)
                .textBody(String.format(IBAN_UPDATE_BODY, ciName, iban, getEnvParam()))
                .htmlBodyFileName("updateIbanNotificationEmail.html")
                .htmlBodyContext(bodyContext)
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();
        this.awsSesClient.sendEmail(messageDetail);
    }

    @Async
    public void notifyIbanDeletion(String ciTaxCode, String iban, String deleteDate) {
        String ciName = getCiName(ciTaxCode);
        Context bodyContext = buildHtmlBodyContext(
                List.of(
                        Pair.of(ENVIRONMENT, getEnvParam()),
                        Pair.of(CI_NAME, ciName),
                        Pair.of(IBAN, iban),
                        Pair.of("deleteDate", deleteDate)));
        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(IBAN_DELETE_SUBJECT)
                .textBody(String.format(IBAN_DELETE_BODY, ciName, iban, deleteDate, getEnvParam()))
                .htmlBodyFileName("deleteIbanNotificationEmail.html")
                .htmlBodyContext(bodyContext)
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();
        this.awsSesClient.sendEmail(messageDetail);
    }

    @Async
    public void notifyIbanRestore(String ciTaxCode, String iban) {
        String ciName = getCiName(ciTaxCode);
        Context bodyContext = buildHtmlBodyContext(
                List.of(
                        Pair.of(ENVIRONMENT, getEnvParam()),
                        Pair.of(CI_NAME, ciName),
                        Pair.of(IBAN, iban)));
        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(IBAN_RESTORE_SUBJECT)
                .textBody(String.format(IBAN_RESTORE_BODY, ciName, iban, getEnvParam()))
                .htmlBodyFileName("restoreDisabledIbanNotificationEmail.html")
                .htmlBodyContext(bodyContext)
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();
        this.awsSesClient.sendEmail(messageDetail);
    }

    private String getCiName(String ciTaxCode) {
        return apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(List.of(ciTaxCode))
                .stream()
                .filter(info -> ciTaxCode.equals(info.getCiTaxCode()))
                .map(CreditorInstitutionInfo::getBusinessName)
                .findFirst()
                .orElse(ciTaxCode);
    }

    private Context buildBundleEmailHtmlBodyContext(String bundleName, String pspName) {
        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        properties.put("bundleName", bundleName);
        properties.put("environment", getEnvParam());
        properties.put("pspName", pspName);
        context.setVariables(properties);
        return context;
    }

    private Context buildHtmlBodyContext(List<Pair<String, String>> textProperties) {
        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        textProperties.forEach(p -> properties.put(p.getKey(), p.getValue()));
        context.setVariables(properties);
        return context;
    }

    private String getEnvParam() {
        if (this.environment.equals("PROD")) {
            return "";
        }
        return String.format(".%s", this.environment.toLowerCase());
    }
}