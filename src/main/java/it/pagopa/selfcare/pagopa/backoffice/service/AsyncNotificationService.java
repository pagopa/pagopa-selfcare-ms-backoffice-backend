package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.InstitutionsClient;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.model.notices.InstitutionUploadData;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.*;

import static it.pagopa.selfcare.pagopa.backoffice.util.MailTextConstants.*;

@Service
public class AsyncNotificationService {

    private final String environment;
    private final AwsSesClient awsSesClient;
    private final InstitutionsClient institutionsClient;

    public AsyncNotificationService(
            @Value("${info.properties.environment}") String environment,
            AwsSesClient awsSesClient,
            InstitutionsClient institutionsClient
    ) {
        this.environment = environment;
        this.awsSesClient = awsSesClient;
        this.institutionsClient = institutionsClient;
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
    public void notifyIbanCreation(String ciTaxCode){
        InstitutionUploadData institutionUploadData = institutionsClient.getInstitutionData(ciTaxCode);
        Context bodyContext = buildHtmlBodyContext(
                List.of(
                        Pair.of("environment", getEnvParam()),
                        Pair.of("ciName", institutionUploadData.getFullName())));
        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(IBAN_CREATE_SUBJECT)
                .textBody(String.format(IBAN_CREATE_BODY, institutionUploadData.getFullName(), getEnvParam()))
                .htmlBodyFileName("createIbanNotificationEmail.html")
                .htmlBodyContext(bodyContext)
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();
        this.awsSesClient.sendEmail(messageDetail);
    }

    @Async
    public void notifyIbanUpdate(String ciTaxCode, String iban){
        InstitutionUploadData institutionUploadData = institutionsClient.getInstitutionData(ciTaxCode);
        Context bodyContext = buildHtmlBodyContext(
                List.of(
                        Pair.of("environment", getEnvParam()),
                        Pair.of("ciName", institutionUploadData.getFullName()),
                        Pair.of("iban", iban)));
        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(IBAN_UPDATE_SUBJECT)
                .textBody(String.format(IBAN_UPDATE_BODY, institutionUploadData.getFullName(), iban, getEnvParam()))
                .htmlBodyFileName("updateIbanNotificationEmail.html")
                .htmlBodyContext(bodyContext)
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();
        this.awsSesClient.sendEmail(messageDetail);
    }

    @Async
    public void notifyIbanDeletion(String ciTaxCode, String iban, String deleteDate){
        InstitutionUploadData institutionUploadData = institutionsClient.getInstitutionData(ciTaxCode);
        Context bodyContext = buildHtmlBodyContext(
                List.of(
                        Pair.of("environment", getEnvParam()),
                        Pair.of("ciName", institutionUploadData.getFullName()),
                        Pair.of("iban", iban),
                        Pair.of("deleteDate", deleteDate)));
        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(IBAN_DELETE_SUBJECT)
                .textBody(String.format(IBAN_DELETE_BODY, institutionUploadData.getFullName(), iban, deleteDate, getEnvParam()))
                .htmlBodyFileName("updateIbanNotificationEmail.html")
                .htmlBodyContext(bodyContext)
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();
        this.awsSesClient.sendEmail(messageDetail);
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
