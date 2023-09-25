package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.headers.Header;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperChannels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.core.*;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ChannelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/channels", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "channels")
public class ChannelController {

    private final ApiConfigService apiConfigService;

    private final ApiConfigSelfcareIntegrationService apiConfigSelfcareIntegrationService;

    private final WrapperService wrapperService;

    private final JiraServiceManagerService jiraServiceManagerService;

    private final AwsSesService awsSesService;

    @Value("${aws.ses.user}")
    private String awsSesUser;


    @Autowired
    public ChannelController(ApiConfigService apiConfigService, ApiConfigSelfcareIntegrationService apiConfigSelfcareIntegrationService, WrapperService wrapperService, JiraServiceManagerService jiraServiceManagerService, AwsSesService awsSesService) {
        this.apiConfigService = apiConfigService;
        this.apiConfigSelfcareIntegrationService = apiConfigSelfcareIntegrationService;
        this.wrapperService = wrapperService;
        this.jiraServiceManagerService = jiraServiceManagerService;
        this.awsSesService = awsSesService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannels}")
    public ChannelsResource getChannels(@ApiParam("${swagger.pageable.number}")
                                        @RequestParam(required = false, defaultValue = "50") Integer limit,
                                        @ApiParam("${swagger.pageable.start}")
                                        @RequestParam(required = true) Integer page,
                                        @ApiParam("${swagger.model.channel.filter}")
                                        @RequestParam(required = false) String code,
                                        @ApiParam("${swagger.model.sort.order}")
                                        @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort) {
        Channels channels = apiConfigService.getChannels(limit, page, code, null, sort);
        ChannelsResource resource = ChannelMapper.toResource(channels);
        return resource;
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createChannel}")
    public WrapperChannelDetailsResource createChannel(@RequestBody @NotNull ChannelDetailsDto channelDetailsDto) {

        final String CREATE_CHANEL_SUBJECT = "Creazione Canale";
        final String CREATE_CHANEL_EMAIL_BODY = String.format("Buongiorno %n%n Il canale %s è stato validato da un operatore e risulta essere attivo%n%nSaluti", channelDetailsDto.getChannelCode());

        PspChannelPaymentTypes pspChannelPaymentTypes = new PspChannelPaymentTypes();
        List<String> paymentTypeList = channelDetailsDto.getPaymentTypeList();
        String channelCode = channelDetailsDto.getChannelCode();
        pspChannelPaymentTypes.setPaymentTypeList(paymentTypeList);

        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        apiConfigService.createChannel(channelDetails);

        WrapperEntitiesOperations<ChannelDetails> response = wrapperService.updateWrapperChannelDetailsByOpt(channelDetails, channelDetailsDto.getNote(), channelDetailsDto.getStatus().name());
        PspChannelPaymentTypes ptResponse = apiConfigService.createChannelPaymentType(pspChannelPaymentTypes, channelCode);
        WrapperChannelDetailsResource resource = ChannelMapper.toResource(response.getWrapperEntityOperationsSortedList().get(0), ptResponse);
        awsSesService.sendEmail(CREATE_CHANEL_SUBJECT, CREATE_CHANEL_EMAIL_BODY,channelDetailsDto.getEmail());
        return resource;
    }

    @GetMapping(value = "/{pspcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public PspChannelsResource getPspChannels(@ApiParam("${swagger.request.pspCode}")
                                              @PathVariable("pspcode") String pspCode) {
        PspChannels pspChannels = apiConfigService.getPspChannels(pspCode);
        PspChannelsResource resource = ChannelMapper.toResource(pspChannels);
        return resource;
    }

    @GetMapping(value = "/details/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ChannelDetailsResource getChannelDetails(@ApiParam("${swagger.request.channelcode}")
                                                    @PathVariable("channelcode") String channelcode) {
        ChannelDetails channelDetails = apiConfigService.getChannelDetails(channelcode);

        PspChannelPaymentTypes ptResponse = apiConfigService.getChannelPaymentTypes(channelcode);
        ChannelDetailsResource resource = ChannelMapper.toResource(channelDetails, ptResponse);

        return resource;
    }

    @GetMapping(value = "/get-details/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ChannelDetailsResource getChannelDetail(@ApiParam("${swagger.request.channelcode}")
                                                   @PathVariable("channelcode") String channelcode) {
        ChannelDetails channelDetail;
        WrapperStatus status;
        String createdBy = "";
        String modifiedBy = "";
        PspChannelPaymentTypes ptResponse = new PspChannelPaymentTypes();
        try {
            WrapperEntitiesOperations<ChannelDetails> result = wrapperService.findById(channelcode);
            createdBy = result.getCreatedBy();
            modifiedBy = result.getModifiedBy();
            channelDetail = result.getWrapperEntityOperationsSortedList().get(0).getEntity();
            status = result.getStatus();
            ptResponse.setPaymentTypeList(result.getWrapperEntityOperationsSortedList().get(0).getEntity().getPaymentTypeList());
        } catch (ResourceNotFoundException e) {
            channelDetail = apiConfigService.getChannelDetails(channelcode);
            ptResponse = apiConfigService.getChannelPaymentTypes(channelcode);
            status = WrapperStatus.APPROVED;
        }
        ChannelDetailsResource resource = ChannelMapper.toResource(channelDetail, ptResponse, status, createdBy, modifiedBy);
        return resource;
    }

    @PostMapping(value = "/{channelcode}/paymenttypes", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createChannelPaymentType}")
    public PspChannelPaymentTypesResource createChannelPaymentType(@ApiParam("${swagger.model.channel.channelCode}") @PathVariable("channelcode") String channelCode,
                                                                   @ApiParam("${swagger.model.PspChannelPaymentTypesResource.list}")
                                                                   @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes) {
        PspChannelPaymentTypes response = apiConfigService.createChannelPaymentType(pspChannelPaymentTypes, channelCode);
        PspChannelPaymentTypesResource resource = ChannelMapper.toResource(response);
        return resource;
    }

    @PutMapping(value = "{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createChannel}")
    public ChannelDetailsResource updateChannel(@RequestBody @NotNull ChannelDetailsDto channelDetailsDto,
                                                @ApiParam("${swagger.model.channel.channelCode}")
                                                @PathVariable("channelcode") String channelCode) {


        final String UPDATE_CHANEL_SUBJECT = "Update Canale";
        final String UPDATE_CHANEL_EMAIL_BODY = String.format("Buongiorno%n%n la modifica per Il canale %s è stata validata da un operatore e risulta essere attiva%n%nSaluti", channelDetailsDto.getChannelCode());

        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        ChannelDetails response = apiConfigService.updateChannel(channelDetails, channelCode);
        wrapperService.updateWrapperChannelDetails(channelDetails, channelDetailsDto.getNote(), channelDetailsDto.getStatus().name(), null);
        ChannelDetailsResource resource = ChannelMapper.toResource(response, null);
        awsSesService.sendEmail(UPDATE_CHANEL_SUBJECT, UPDATE_CHANEL_EMAIL_BODY,channelDetailsDto.getEmail());
        return resource;
    }


    @GetMapping(value = "configuration/paymenttypes", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getPaymentTypes}")
    public PaymentTypesResource getPaymentTypes() {


        PaymentTypes response = apiConfigService.getPaymentTypes();
        PaymentTypesResource resource = ChannelMapper.toResource(response);

        return resource;
    }

    @DeleteMapping(value = "/{channelcode}/{paymenttypecode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.deleteChannelPaymentType}")
    public void deleteChannelPaymentType(@ApiParam("${swagger.model.channel.channelCode}") @PathVariable("channelcode") String channelCode,
                                         @ApiParam("${swagger.request.paymentTypeCode}")
                                         @PathVariable("paymenttypecode") String paymentTypeCode) {

        apiConfigService.deleteChannelPaymentType(channelCode, paymentTypeCode);
    }

    @GetMapping(value = "paymenttypes/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannelPaymentTypes}")
    public PspChannelPaymentTypesResource getChannelPaymentTypes(@ApiParam("${swagger.model.channel.channelCode}")
                                                                 @PathVariable("channelcode") String channelCode) {

        PspChannelPaymentTypes response = apiConfigService.getChannelPaymentTypes(channelCode);
        PspChannelPaymentTypesResource resource = ChannelMapper.toResource(response);


        return resource;
    }

    @DeleteMapping(value = "psp/{channelcode}/{pspcode}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.deletePaymentServiceProvidersChannels}")
    public void deletePaymentServiceProvidersChannels(@ApiParam("${swagger.model.channel.channelCode}")
                                                      @PathVariable("channelcode") String channelCode,
                                                      @ApiParam("${swagger.request.pspCode}")
                                                      @PathVariable("pspcode") String pspCode) {

        apiConfigService.deletePaymentServiceProvidersChannels(pspCode, channelCode);
    }

    @PutMapping(value = "psp/{channelcode}/{pspcode}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.updatePaymentServiceProvidersChannels}")
    public PspChannelPaymentTypesResource updatePaymentServiceProvidersChannels(@ApiParam("${swagger.model.channel.channelCode}")
                                                                                @PathVariable("channelcode") String channelCode,
                                                                                @ApiParam("${swagger.request.pspCode}")
                                                                                @PathVariable("pspcode") String pspCode,
                                                                                @ApiParam("${swagger.model.PspChannelPaymentTypesResource.list}")
                                                                                @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes) {

        PspChannelPaymentTypes response = apiConfigService.updatePaymentServiceProvidersChannels(pspCode, channelCode, pspChannelPaymentTypes);
        PspChannelPaymentTypesResource resource = ChannelMapper.toResource(response);
        return resource;
    }

    @DeleteMapping(value = "/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.deleteChannel}")
    public void deleteChannel(@ApiParam("${swagger.request.channelcode}")
                              @PathVariable("channelcode") String channelcode) {

        apiConfigService.deleteChannel(channelcode);
    }

    @GetMapping(value = "/{brokerpspcode}/paymentserviceproviders", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getPspBrokerPsp}")
    public PaymentServiceProvidersResource getPspBrokerPsp(@ApiParam("${swagger.request.limit}")
                                                           @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                           @ApiParam("${swagger.request.page}")
                                                           @RequestParam Integer page,
                                                           @ApiParam("${swagger.request.brokerpspcode}")
                                                           @PathVariable("brokerpspcode") String brokerPspCode) {

        PaymentServiceProviders response = apiConfigService.getPspBrokerPsp(limit, page, brokerPspCode);
        PaymentServiceProvidersResource resource = ChannelMapper.toResource(response);


        return resource;
    }


    @GetMapping(value = "/csv", produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannelsCSV}")
    public Resource getChannelsCSV(HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"channels.csv\"");

        Resource resource = apiConfigService.getChannelsCSV();
        return resource;
    }

    @GetMapping(value = "/{channelcode}/psp", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannelPaymentServiceProviders}")
    public ChannelPspListResource getChannelPaymentServiceProviders(@ApiParam("${swagger.pageable.number}")
                                                                    @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                                    @ApiParam("${swagger.pageable.start}")
                                                                    @RequestParam(required = true) Integer page,
                                                                    @ApiParam("${swagger.request.channelcode}")
                                                                    @PathVariable("channelcode") String channelCode) {

        ChannelPspList response = apiConfigService.getChannelPaymentServiceProviders(limit, page, channelCode);
        ChannelPspListResource resource = ChannelMapper.toResource(response);

        return resource;
    }

    @PostMapping(value = "/brokerspsp", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createBrokerPsp}")
    public BrokerPspDetailsResource createBrokerPsp(@RequestBody @NotNull BrokerPspDetailsDto brokerPspDetailsDto) {



        BrokerPspDetails brokerPspDetails = ChannelMapper.fromBrokerPspDetailsDto(brokerPspDetailsDto);
        BrokerPspDetails response = apiConfigService.createBrokerPsp(brokerPspDetails);


        BrokerPspDetailsResource resource = ChannelMapper.toResource(response);

        return resource;
    }

    @PostMapping(value = "/psp", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createPaymentServiceProvider}")
    public PaymentServiceProviderDetailsResource createPaymentServiceProvider(@RequestBody @NotNull PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto) {


        PaymentServiceProviderDetails paymentServiceProviderDetails = ChannelMapper.fromPaymentServiceProviderDetailsDto(paymentServiceProviderDetailsDto);
        PaymentServiceProviderDetails response = apiConfigService.createPaymentServiceProvider(paymentServiceProviderDetails);


        PaymentServiceProviderDetailsResource resource = ChannelMapper.toResource(response);

        return resource;
    }

    @PostMapping(value = "/pspdirect", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createPaymentServiceProvider}")
    public PaymentServiceProviderDetailsResource createPSPDirect(@RequestBody @NotNull PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto) {


        Map<String, Object> res = ChannelMapper.fromPaymentServiceProviderDetailsDtoToMap(paymentServiceProviderDetailsDto);
        BrokerPspDetails brokerPspDetails = (BrokerPspDetails) res.get("broker");
        PaymentServiceProviderDetails paymentServiceProviderDetails = (PaymentServiceProviderDetails) res.get("psp");

        apiConfigService.createBrokerPsp(brokerPspDetails);
        PaymentServiceProviderDetails responsePSP = apiConfigService.createPaymentServiceProvider(paymentServiceProviderDetails);


        PaymentServiceProviderDetailsResource resource = ChannelMapper.toResource(responsePSP);

        return resource;
    }

    @GetMapping(value = "/{pspcode}/generate", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannelCode}")
    public ChannelCodeResource getChannelCode(@ApiParam("${swagger.request.pspCode}")
                                              @PathVariable("pspcode") String pspCode) {
        String result = apiConfigService.generateChannelCode(pspCode);
        ChannelCodeResource channelCode = new ChannelCodeResource(result);
        return channelCode;
    }

    @GetMapping(value = "/{pspcode}/generateV2", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannelCode}")
    public ChannelCodeResource getChannelCodeV2(@ApiParam("${swagger.request.pspCode}")
                                                @PathVariable("pspcode") String pspCode) {

        Channels channels = apiConfigService.getChannels(100, 0, pspCode, null, "ASC");
        WrapperChannels responseApiConfig = ChannelMapper.toWrapperChannels(channels);
        WrapperEntitiesList mongoList = wrapperService.findByIdLikeOrTypeOrBrokerCode(pspCode, WrapperType.CHANNEL, null, 0, 100);
        WrapperChannels responseMongo = ChannelMapper.toWrapperChannels(mongoList);
        WrapperChannels channelsMergedAndSorted = apiConfigService.mergeAndSortWrapperChannels(responseApiConfig, responseMongo, "ASC");
        String result = apiConfigService.generateChannelCodeV2(channelsMergedAndSorted.getChannelList(), pspCode);

        ChannelCodeResource channelCode = new ChannelCodeResource(result);
        return channelCode;
    }

    @GetMapping(value = "/psp/{pspcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getPSPDetails}")
    public PaymentServiceProviderDetailsResource getPSPDetails(@ApiParam("${swagger.request.pspCode}")
                                                               @PathVariable("pspcode") String pspCode) {
        PaymentServiceProviderDetails paymentServiceProviderDetails = apiConfigService.getPSPDetails(pspCode);
        PaymentServiceProviderDetailsResource resource = ChannelMapper.toResource(paymentServiceProviderDetails);
        return resource;
    }


    @PostMapping(value = "/create-wrapperChannel", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createWrapperChannelDetails}")
    public WrapperEntitiesOperations createWrapperChannelDetails(@RequestBody
                                                                 @Valid
                                                                 WrapperChannelDetailsDto wrapperChannelDetailsDto) {
        final String CREATE_CHANNEL_SUMMARY = "Validazione canale creazione: %s";
        final String CREATE_CHANEL_DESCRIPTION = "Il canale %s deve essere validato: %s";
        WrapperEntitiesOperations createdWrapperEntities = wrapperService.
                createWrapperChannelDetails(ChannelMapper.
                        fromWrapperChannelDetailsDto(wrapperChannelDetailsDto), wrapperChannelDetailsDto.getNote(), wrapperChannelDetailsDto.getStatus().name());
        jiraServiceManagerService.createTicket(String.format(CREATE_CHANNEL_SUMMARY, wrapperChannelDetailsDto.getChannelCode()),
                String.format(CREATE_CHANEL_DESCRIPTION, wrapperChannelDetailsDto.getChannelCode(), wrapperChannelDetailsDto.getValidationUrl()));
        return createdWrapperEntities;
    }

    @PutMapping(value = "/update-wrapperChannel", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.updateWrapperChannelDetails}")
    public WrapperEntitiesOperations updateWrapperChannelDetails(@RequestBody
                                                                 @Valid
                                                                 ChannelDetailsDto channelDetailsDto) {
        final String CREATE_CHANNEL_SUMMARY = "Validazione modifica canale: %s";
        final String CREATE_CHANEL_DESCRIPTION = "Il canale %s modificato dal broker %s deve essere validato: %s";
        WrapperEntitiesOperations createdWrapperEntities = wrapperService.
                updateWrapperChannelDetails(ChannelMapper.
                        fromChannelDetailsDto(channelDetailsDto), channelDetailsDto.getNote(), channelDetailsDto.getStatus().name(), null);
        jiraServiceManagerService.createTicket(String.format(CREATE_CHANNEL_SUMMARY, channelDetailsDto.getChannelCode()),
                String.format(CREATE_CHANEL_DESCRIPTION, channelDetailsDto.getChannelCode(), channelDetailsDto.getBrokerPspCode(), channelDetailsDto.getValidationUrl()));
        return createdWrapperEntities;
    }

    @GetMapping(value = "/get-wrapperEntities/{code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getWrapperEntities}")
    public WrapperEntitiesOperations getWrapperEntities(@ApiParam("${swagger.request.code}") @PathVariable("code") String code) {
        WrapperEntitiesOperations result = wrapperService.findById(code);
        return result;
    }

    @PutMapping(value = "/update-wrapperChannelByOpt", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.updateWrapperChannelDetailsByOpt}")
    public WrapperEntitiesOperations updateWrapperChannelDetailsByOpt(@RequestBody
                                                                      @Valid
                                                                      ChannelDetailsDto channelDetailsDto) {
        WrapperEntitiesOperations createdWrapperEntities = wrapperService.
                updateWrapperChannelDetailsByOpt(ChannelMapper.
                        fromChannelDetailsDto(channelDetailsDto), channelDetailsDto.getNote(), channelDetailsDto.getStatus().name());
        return createdWrapperEntities;
    }

    @GetMapping(value = "/wfespplugins", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.updateWrapperChannelDetailsByOpt}")
    public ResponseEntity<WfespPluginConfs> getWfespPlugins() {
        WfespPluginConfs wfespPluginConfs = apiConfigService.getWfespPlugins();
        return ResponseEntity.ok(wfespPluginConfs);
    }

    @GetMapping(value = "get-wrapper/{wrapperType}/{wrapperStatus}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getWrapperByTypeAndStatus}")
    public WrapperEntitiesList getWrapperByTypeAndStatus(@ApiParam("${swagger.request.limit}")
                                                         @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                         @ApiParam("${swagger.request.page}")
                                                         @RequestParam Integer page,
                                                         @ApiParam("${swagger.request.wrapperType}")
                                                         @PathVariable("wrapperType") WrapperType wrapperType,
                                                         @ApiParam("${swagger.request.wrapperStatus}")
                                                         @PathVariable(required = false, value = "wrapperStatus") WrapperStatus wrapperStatus,
                                                         @ApiParam("${swagger.request.brokerCode}")
                                                         @RequestParam(required = false, value = "brokerCode") String brokerCode,
                                                         @ApiParam("${swagger.request.idLike}")
                                                         @RequestParam(required = false, value = "idLike") String idLike,
                                                         @ApiParam("${swagger.request.sorting}")
                                                         @RequestParam(required = false, value = "sorting") String sorting) {
        WrapperEntitiesList response = wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(wrapperStatus, wrapperType, brokerCode, idLike, page, limit, sorting);


        return response;
    }

    @GetMapping(value = "getAllChannels", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getAllChannelsMerged}")
    public WrapperChannelsResource getAllChannelsMerged(@ApiParam("${swagger.request.limit}")
                                                        @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                        @ApiParam("${swagger.model.channel.code}")
                                                        @RequestParam(required = false, value = "channelcodefilter") String channelcode,
                                                        @ApiParam("${swagger.request.brokerCode}")
                                                        @RequestParam(required = false, value = "brokerCode") String brokerCode,
                                                        @ApiParam("${swagger.request.page}")
                                                        @RequestParam Integer page,
                                                        @ApiParam("${swagger.request.sorting}")
                                                        @RequestParam(required = false, value = "sorting") String sorting) {

        Channels channels = apiConfigService.getChannels(limit, page, channelcode, brokerCode, sorting);
        WrapperChannels responseApiConfig = ChannelMapper.toWrapperChannels(channels);
        WrapperEntitiesList mongoList = wrapperService.findByIdLikeOrTypeOrBrokerCode(channelcode, WrapperType.CHANNEL, brokerCode, page, limit);

        WrapperChannels responseMongo = ChannelMapper.toWrapperChannels(mongoList);
        WrapperChannels channelsMergedAndSorted = apiConfigService.mergeAndSortWrapperChannels(responseApiConfig, responseMongo, sorting);
        WrapperChannelsResource response = ChannelMapper.toWrapperChannelsResource(channelsMergedAndSorted);

        return response;
    }

    @GetMapping(value = "{brokerId}/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getChannelDetailsListByBroker}")
    public ChannelDetailsResourceList getChannelDetailsListByBroker(@PathVariable("brokerId") String brokerId,
                                                                    @RequestParam(required = false) String channelId,
                                                                    @RequestParam(required = false, defaultValue = "10") Integer limit,
                                                                    @RequestParam(required = false, defaultValue = "0") Integer page) {
        ChannelDetailsList response = apiConfigSelfcareIntegrationService.getChannelsDetailsListByBroker(brokerId, channelId, limit, page);
        ChannelDetailsResourceList resource = ChannelMapper.fromChannelDetailsList(response);

        return resource;
    }


    @GetMapping(value = "/getBrokersPsp", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getBrokersPsp}")
    public BrokersPspResource getBrokersPsp(@ApiParam("${swagger.request.limit}")
                                            @RequestParam(required = false, defaultValue = "50") Integer limit,
                                            @ApiParam("${swagger.request.page}")
                                            @RequestParam Integer page,
                                            @ApiParam("${swagger.request.broker.code}")
                                            @RequestParam(required = false, name = "code") String filterByCode,
                                            @ApiParam("${swagger.request.broker.name}")
                                            @RequestParam(required = false, name = "name") String filterByName,
                                            @ApiParam("${swagger.request.broker.ordering}")
                                            @RequestParam(required = false, name = "orderby", defaultValue = "CODE") String orderBy,
                                            @ApiParam("${swagger.request.sorting}")
                                            @RequestParam(required = false, value = "sorting", defaultValue = "DESC") String sorting) {



        BrokersPsp response = apiConfigService.getBrokersPsp(limit, page, filterByCode, filterByName, orderBy, sorting);


        BrokersPspResource resource = ChannelMapper.toResource(response);

        return resource;
    }

    @GetMapping(value = "/brokerdetails", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getBrokerPsp}")
    public BrokerPspDetailsResource getBrokerPsp(@ApiParam("swagger.request.brokerpspcode")
                                                 @RequestParam(required = false, name = "brokerpspcode") String brokerPspCode) {


        BrokerPspDetails response = apiConfigService.getBrokerPsp(brokerPspCode);

        BrokerPspDetailsResource resource = ChannelMapper.toResource(response);


        return resource;
    }



}
