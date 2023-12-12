package it.pagopa.selfcare.pagopa.backoffice.util;

import it.pagopa.selfcare.pagopa.backoffice.model.SelfCareUser;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperChannel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperChannels;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Utility {

    private Utility() {
    }


    public static String extractUserIdFromAuth(Authentication authentication) {
        String userIdForAuth = "";
        if(authentication != null && authentication.getPrincipal() instanceof SelfCareUser) {
            var user = (SelfCareUser) authentication.getPrincipal();
            userIdForAuth = user.getId();
        }
        return userIdForAuth;
    }

    public static WrapperChannels mergeAndSortWrapperChannels(WrapperChannels channelFromApiConfig, WrapperChannels channelFromLocal, String sorting) {
        List<WrapperChannel> mergedList = new ArrayList<>();
        mergedList.addAll(channelFromLocal.getChannelList());
        mergedList.addAll(
                channelFromApiConfig.getChannelList().stream()
                        .filter(obj2 -> channelFromLocal.getChannelList().stream()
                                .noneMatch(obj1 -> Objects.equals(obj1.getChannelCode(), obj2.getChannelCode())))
                        .collect(Collectors.toList())
        );

        if("asc".equalsIgnoreCase(sorting)) {
            mergedList.sort(Comparator.comparing(WrapperChannel::getChannelCode));
        } else if("desc".equalsIgnoreCase(sorting)) {
            mergedList.sort(Comparator.comparing(WrapperChannel::getChannelCode, Comparator.reverseOrder()));
        }
        WrapperChannels result = new WrapperChannels();
        result.setChannelList(mergedList);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setLimit(channelFromApiConfig.getPageInfo().getLimit());
        pageInfo.setTotalPages(channelFromApiConfig.getPageInfo().getTotalPages());
        pageInfo.setPage(channelFromApiConfig.getPageInfo().getPage());
        pageInfo.setItemsFound(mergedList.size());
        pageInfo.setTotalItems(channelFromApiConfig.getPageInfo().getTotalItems());
        result.setPageInfo(pageInfo);
        return result;
    }
}
