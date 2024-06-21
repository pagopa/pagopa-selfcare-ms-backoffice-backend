package it.pagopa.selfcare.pagopa.backoffice.util;

import it.pagopa.selfcare.pagopa.backoffice.entity.PspLegacyEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentServiceProviderDetails;
import it.pagopa.selfcare.pagopa.backoffice.repository.PspLegacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static it.pagopa.selfcare.pagopa.backoffice.exception.AppError.PSP_CODE_NOT_FOUND;

@Component
public class LegacyPspCodeUtil {

    private final PspLegacyRepository pspLegacyRepository;

    @Autowired
    public LegacyPspCodeUtil(PspLegacyRepository pspLegacyRepository) {
        this.pspLegacyRepository = pspLegacyRepository;
    }

    /**
     * We convert the taxCode in a psp code using the collection for legacy PSP,
     * otherwise we create the psp code using the concatenation 'PSP'+taxCode
     *
     * @param taxCode  fiscal code of a PSP
     * @param asBroker true if the PSP acts as a broker (PSP Direct)
     * @return the PSP code
     */
    public String retrievePspCode(String taxCode, boolean asBroker) {
        String pspCode = null;
        PspLegacyEntity pspLegacyEntity = pspLegacyRepository.findByCf(taxCode).orElse(null);

        if(pspLegacyEntity != null) {
            if(asBroker) {
                // if psp acts as a broker we search first in the BIC list and then in the ABI list
                if(bicIsPresent(pspLegacyEntity)) {
                    pspCode = pspLegacyEntity.getBic().get(0);
                } else if(abiIsPresent(pspLegacyEntity)) {
                    pspCode = pspLegacyEntity.getAbi().get(0);
                }
            } else {
                // if psp doesn't act as a broker we search first in the ABI list and then in the BIC list
                if(abiIsPresent(pspLegacyEntity)) {
                    pspCode = pspLegacyEntity.getAbi().get(0);
                } else if(bicIsPresent(pspLegacyEntity)) {
                    pspCode = pspLegacyEntity.getBic().get(0);
                }
            }
        }

        if (pspCode == null) {
            throw new AppException(PSP_CODE_NOT_FOUND, taxCode);
        }

        return pspCode;
    }

    private static boolean abiIsPresent(PspLegacyEntity pspLegacyEntity) {
        return pspLegacyEntity.getAbi() != null && !pspLegacyEntity.getAbi().isEmpty();
    }

    private static boolean bicIsPresent(PspLegacyEntity pspLegacyEntity) {
        return pspLegacyEntity.getBic() != null && !pspLegacyEntity.getBic().isEmpty();
    }

    public void upsertPspLegacy(PaymentServiceProviderDetails pspDetails) {
        PspLegacyEntity entity;
        var optional = pspLegacyRepository.findByCf(pspDetails.getTaxCode());
        if(optional.isPresent()) {
            // check if the entity already exists
            entity = optional.get();
        } else {
            // build the new entity to save
            var builder = PspLegacyEntity.builder()
                    .cf(pspDetails.getTaxCode());

            // if the abi is present we need to add it into the list
            if(pspDetails.getAbi() != null && !pspDetails.getAbi().isEmpty() && !pspDetails.getAbi().equals("N/A")) {
                builder.abi(List.of(pspDetails.getPspCode()));
            }
            // if the bic is present we need to add it into the list
            else if(pspDetails.getBic() != null && !pspDetails.getBic().isEmpty()) {
                builder.bic(List.of(pspDetails.getPspCode()));
            }
            entity = builder.build();
        }

        pspLegacyRepository.save(entity);
    }

}
