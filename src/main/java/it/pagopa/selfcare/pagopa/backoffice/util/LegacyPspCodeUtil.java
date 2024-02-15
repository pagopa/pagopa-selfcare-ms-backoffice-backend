package it.pagopa.selfcare.pagopa.backoffice.util;

import it.pagopa.selfcare.pagopa.backoffice.entity.PspLegacyEntity;
import it.pagopa.selfcare.pagopa.backoffice.repository.PspLegacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LegacyPspCodeUtil {

    private final PspLegacyRepository pspLegacyRepository;

    private final static String PSP_PREFIX = "PSP";

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
        String pspCode = PSP_PREFIX.concat(taxCode);
        PspLegacyEntity pspLegacyEntity = pspLegacyRepository.findByCf(taxCode).orElse(null);

        if(pspLegacyEntity != null) {
            if(abiIsPresent(pspLegacyEntity)) {
                pspCode = pspLegacyEntity.getAbi().get(0);
            }
            // if direct we search for a BIC psp, but if it doesn't exist we can use the ABI psp
            if(asBroker && bicIsPresent(pspLegacyEntity)) {
                pspCode = pspLegacyEntity.getBic().get(0);
            }
        }
        return pspCode;
    }

    private static boolean abiIsPresent(PspLegacyEntity pspLegacyEntity) {
        return pspLegacyEntity.getAbi() != null && !pspLegacyEntity.getAbi().isEmpty();
    }

    private static boolean bicIsPresent(PspLegacyEntity pspLegacyEntity) {
        return pspLegacyEntity.getBic() != null && !pspLegacyEntity.getBic().isEmpty();
    }

}
