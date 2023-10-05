package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.*;
import it.pagopa.selfcare.pagopa.backoffice.web.model.gec.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GecMapper {

    public static BundleResource toResource(Bundle bundle){
        if(bundle == null){
            return null;
        }
        BundleResource response = new BundleResource();

        response.setIdBundle(bundle.getIdBundle());
        response.setName(bundle.getName());
        response.setIdPsp(bundle.getIdPsp());
        response.setTouchpoint(bundle.getTouchpoint());
        response.setType(bundle.getType());
        response.setPaymentAmount(bundle.getPaymentAmount());
        response.setPaymentType(bundle.getPaymentType());
        response.setMaxPaymentAmount(bundle.getMaxPaymentAmount());
        response.setDescription(bundle.getDescription());
        response.setIdCiBundle(bundle.getIdCiBundle());
        response.setMinPaymentAmount(bundle.getMinPaymentAmount());

        response.setValidityDateFrom(bundle.getValidityDateFrom());
        response.setValidityDateTo(bundle.getValidityDateTo());
        response.setLastUpdatedDate(bundle.getLastUpdatedDate());
        response.setInsertedDate(bundle.getInsertedDate());

        List<String> list = new ArrayList<>();
        if (bundle.getTransferCategoryList() != null) {
            list.addAll(bundle.getTransferCategoryList());
        }
        response.setTransferCategoryList(list);

        return response;
    }

    public static BundlesResource toResource(Bundles bundles){
        if(bundles == null){
            return null;
        }

        BundlesResource bundlesResource = new BundlesResource();

        bundlesResource.setBundles(bundles.getBundles().stream()
                .map(GecMapper::toResource)
                .collect(Collectors.toList()));

        return bundlesResource;
    }

    public static TouchpointResource toResource(Touchpoint touchpoints){
        if(touchpoints == null){
            return null;
        }
        TouchpointResource response = new TouchpointResource();

        response.setId(touchpoints.getId());
        response.setName(touchpoints.getName());
        response.setCreatedDate(touchpoints.getCreatedDate());

        return response;
    }

    public static TouchpointsResource toResource(Touchpoints touchpoints){
        if(touchpoints == null){
            return null;
        }

        TouchpointsResource bundlesResource = new TouchpointsResource();

        bundlesResource.setTouchpoints(touchpoints.getTouchpoints().stream()
                .map(GecMapper::toResource)
                .collect(Collectors.toList()));

        return bundlesResource;
    }

    public static BundleCreate fromDto(BundleDto bundleDto){

        if(bundleDto == null){
            return null;
        }

        BundleCreate bundleCreate = new BundleCreate();

        bundleCreate.setAbi(bundleDto.getAbi());
        bundleCreate.setIdCdi(bundleDto.getIdCdi());
        bundleCreate.setIdBrokerPsp(bundleDto.getIdBrokerPsp());
        bundleCreate.setIdChannel(bundleDto.getIdChannel());
        bundleCreate.setName(bundleDto.getName());
        bundleCreate.setDescription(bundleDto.getDescription());
        bundleCreate.setPspBusinessName(bundleDto.getPspBusinessName());
        bundleCreate.setPaymentAmount(bundleDto.getPaymentAmount());
        bundleCreate.setMinPaymentAmount(bundleDto.getMinPaymentAmount());
        bundleCreate.setMaxPaymentAmount(bundleDto.getMaxPaymentAmount());
        bundleCreate.setPaymentType(bundleDto.getPaymentType());
        bundleCreate.setDigitalStamp(bundleDto.getDigitalStamp());
        bundleCreate.setDigitalStampRestriction(bundleDto.getDigitalStampRestriction());
        bundleCreate.setTouchpoint(bundleDto.getTouchpoint());
        bundleCreate.setType(bundleDto.getType());
        bundleCreate.setValidityDateFrom(bundleDto.getValidityDateFrom());
        bundleCreate.setValidityDateTo(bundleDto.getValidityDateTo());

        List<String> list = new ArrayList<>();
        if (bundleDto.getTransferCategoryList() != null) {
            list.addAll(bundleDto.getTransferCategoryList());
        }
        bundleCreate.setTransferCategoryList(list);

        return bundleCreate;
    }
}
