package it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim.model;


import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.resourcemanager.apimanagement.fluent.models.GroupContractProperties;
import com.azure.resourcemanager.apimanagement.fluent.models.UserContractInner;
import com.azure.resourcemanager.apimanagement.models.*;

import java.time.OffsetDateTime;
import java.util.List;

public class DummyUserContract implements UserContract {
    @Override
    public String id() {
        return "id";
    }

    @Override
    public String name() {
        return "name";
    }

    @Override
    public String type() {
        return null;
    }

    @Override
    public String firstName() {
        return "firstName";
    }

    @Override
    public String lastName() {
        return "lastName";
    }

    @Override
    public String email() {
        return "email";
    }

    @Override
    public OffsetDateTime registrationDate() {
        return null;
    }

    @Override
    public List<GroupContractProperties> groups() {
        return null;
    }

    @Override
    public UserState state() {
        return null;
    }

    @Override
    public String note() {
        return null;
    }

    @Override
    public List<UserIdentityContract> identities() {
        return null;
    }

    @Override
    public UserContractInner innerModel() {
        return null;
    }

    @Override
    public Update update() {
        return null;
    }

    @Override
    public UserContract refresh() {
        return null;
    }

    @Override
    public UserContract refresh(Context context) {
        return null;
    }

    @Override
    public GenerateSsoUrlResult generateSsoUrl() {
        return null;
    }

    @Override
    public Response<GenerateSsoUrlResult> generateSsoUrlWithResponse(Context context) {
        return null;
    }

    @Override
    public UserTokenResult getSharedAccessToken(UserTokenParameters parameters) {
        return null;
    }

    @Override
    public Response<UserTokenResult> getSharedAccessTokenWithResponse(UserTokenParameters parameters, Context context) {
        return null;
    }
}
