package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
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

import javax.management.monitor.StringMonitor;
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
        log.trace("getchannels start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getchannels code filter = {}, xRequestId = {}", code, xRequestId);
        Channels channels = apiConfigService.getChannels(limit, page, code, null, sort, xRequestId);
        ChannelsResource resource = ChannelMapper.toResource(channels);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getchannels result = {}", resource);
        log.trace("getchannels end");
        return resource;
    }

    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createChannel}")
    public WrapperChannelDetailsResource createChannel(@RequestBody @NotNull ChannelDetailsDto channelDetailsDto) {
        log.trace("createChannel start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("createChannel code channelDetailsDto = {}, xRequestId = {}", channelDetailsDto, xRequestId);

        final String CREATE_CHANEL_SUBJECT = "Creazione Canale";
        final String CREATE_CHANEL_EMAIL_BODY = String.format("Buongiorno\n\nIl canale %s è stato validato da un operatore e risulta essere attivo\n\nSaluti", channelDetailsDto.getChannelCode());

        PspChannelPaymentTypes pspChannelPaymentTypes = new PspChannelPaymentTypes();
        List<String> paymentTypeList = channelDetailsDto.getPaymentTypeList();
        String channelCode = channelDetailsDto.getChannelCode();
        pspChannelPaymentTypes.setPaymentTypeList(paymentTypeList);

        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        apiConfigService.createChannel(channelDetails, xRequestId);
        log.trace("created channel in apiConfig");

        WrapperEntitiesOperations<ChannelDetails> response = wrapperService.updateWrapperChannelDetailsByOpt(channelDetails, channelDetailsDto.getNote(), channelDetailsDto.getStatus().name());
        PspChannelPaymentTypes ptResponse = apiConfigService.createChannelPaymentType(pspChannelPaymentTypes, channelCode, xRequestId);
        WrapperChannelDetailsResource resource = ChannelMapper.toResource(response.getWrapperEntityOperationsSortedList().get(0), ptResponse);
        awsSesService.sendEmail(channelDetailsDto.getPspEmail(), CREATE_CHANEL_SUBJECT, CREATE_CHANEL_EMAIL_BODY);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createChannel result = {}", resource);
        log.trace("createChannel end");
        return resource;
    }

    @GetMapping(value = "/{pspcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public PspChannelsResource getPspChannels(@ApiParam("${swagger.request.pspCode}")
                                              @PathVariable("pspcode") String pspCode) {
        log.trace("getPspChannels start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getPspChannels pspcode = {}, xRequestId = {}", pspCode, xRequestId);
        PspChannels pspChannels = apiConfigService.getPspChannels(pspCode, xRequestId);
        PspChannelsResource resource = ChannelMapper.toResource(pspChannels);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getPspChannels result = {}", resource);
        log.trace("getPspChannels end");
        return resource;
    }

    @GetMapping(value = "/details/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ChannelDetailsResource getChannelDetails(@ApiParam("${swagger.request.channelcode}")
                                                    @PathVariable("channelcode") String channelcode) {
        log.trace("getChannelDetails start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getChannelDetails channelcode = {}, xRequestId = {}", channelcode, xRequestId);
        ChannelDetails channelDetails = apiConfigService.getChannelDetails(channelcode, xRequestId);

        PspChannelPaymentTypes ptResponse = apiConfigService.getChannelPaymentTypes(channelcode, xRequestId);
        ChannelDetailsResource resource = ChannelMapper.toResource(channelDetails, ptResponse);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getChannelDetails result = {}", resource);
        log.trace("getChannelDetails end");
        return resource;
    }

    @GetMapping(value = "/get-details/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ChannelDetailsResource getChannelDetail(@ApiParam("${swagger.request.channelcode}")
                                                   @PathVariable("channelcode") String channelcode) {
        log.trace("getChannelDetail start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getChannelDetail channelcode = {}, xRequestId = {}", channelcode, xRequestId);
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
            channelDetail = apiConfigService.getChannelDetails(channelcode, xRequestId);
            ptResponse = apiConfigService.getChannelPaymentTypes(channelcode, xRequestId);
            status = WrapperStatus.APPROVED;
        }
        ChannelDetailsResource resource = ChannelMapper.toResource(channelDetail, ptResponse, status, createdBy, modifiedBy);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getChannelDetails result = {}", resource);
        log.trace("getChannelDetail end");
        return resource;
    }

    @PostMapping(value = "/{channelcode}/paymenttypes", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createChannelPaymentType}")
    public PspChannelPaymentTypesResource createChannelPaymentType(@ApiParam("${swagger.model.channel.channelCode}") @PathVariable("channelcode") String channelCode,
                                                                   @ApiParam("${swagger.model.PspChannelPaymentTypesResource.list}")
                                                                   @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes) {
        log.trace("createChannelPaymentType start");
        String uuid = UUID.randomUUID().toString();
        log.debug("createChannelPaymentType code pspChannelPaymentTypes = {}, uuid {}", pspChannelPaymentTypes, uuid);
        PspChannelPaymentTypes response = apiConfigService.createChannelPaymentType(pspChannelPaymentTypes, channelCode, uuid);
        PspChannelPaymentTypesResource resource = ChannelMapper.toResource(response);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createChannelPaymentType result = {}", resource);
        log.trace("createChannelPaymentType end");
        return resource;
    }

    @PutMapping(value = "{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createChannel}")
    public ChannelDetailsResource updateChannel(@RequestBody @NotNull ChannelDetailsDto channelDetailsDto,
                                                @ApiParam("${swagger.model.channel.channelCode}")
                                                @PathVariable("channelcode") String channelCode) {
        log.trace("updateChannel start");
        String uuid = UUID.randomUUID().toString();
        log.debug("updateChannel code channelDetailsDto = {} , uuid {}", channelDetailsDto, uuid);

        final String UPDATE_CHANEL_SUBJECT = "Update Canale";
        final String UPDATE_CHANEL_EMAIL_BODY = String.format("Buongiorno\n\n la modifica per Il canale %s è stata validata da un operatore e risulta essere attiva\n\nSaluti", channelDetailsDto.getChannelCode());

        ChannelDetails channelDetails = ChannelMapper.fromChannelDetailsDto(channelDetailsDto);
        ChannelDetails response = apiConfigService.updateChannel(channelDetails, channelCode, uuid);
        wrapperService.updateWrapperChannelDetails(channelDetails, channelDetailsDto.getNote(), channelDetailsDto.getStatus().name(), null);
        ChannelDetailsResource resource = ChannelMapper.toResource(response, null);
        awsSesService.sendEmail(channelDetailsDto.getPspEmail(), UPDATE_CHANEL_SUBJECT, UPDATE_CHANEL_EMAIL_BODY);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "updateChannel result = {}", resource);
        log.trace("updateChannel end");
        return resource;
    }


    @GetMapping(value = "configuration/paymenttypes", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getPaymentTypes}")
    public PaymentTypesResource getPaymentTypes() {
        log.trace("getPaymentTypes start");

        log.debug("getPaymentTypes code getPaymentTypes");
        String uuid = UUID.randomUUID().toString();
        log.debug("getPaymentTypes uuid {}", uuid);
        PaymentTypes response = apiConfigService.getPaymentTypes(uuid);
        PaymentTypesResource resource = ChannelMapper.toResource(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getPaymentTypes result = {}", resource);
        log.trace("getPaymentTypes end");
        return resource;
    }

    @DeleteMapping(value = "/{channelcode}/{paymenttypecode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.deleteChannelPaymentType}")
    public void deleteChannelPaymentType(@ApiParam("${swagger.model.channel.channelCode}") @PathVariable("channelcode") String channelCode,
                                         @ApiParam("${swagger.request.paymentTypeCode}")
                                         @PathVariable("paymenttypecode") String paymentTypeCode) {
        log.trace("deleteChannelPaymentType start");
        String uuid = UUID.randomUUID().toString();
        log.debug("deleteChannelPaymentType code paymentTypeCode = {}, channel = {}, uuid {}", paymentTypeCode, channelCode, uuid);
        apiConfigService.deleteChannelPaymentType(channelCode, paymentTypeCode, uuid);
        log.trace("deleteChannelPaymentType end");
    }

    @GetMapping(value = "paymenttypes/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannelPaymentTypes}")
    public PspChannelPaymentTypesResource getChannelPaymentTypes(@ApiParam("${swagger.model.channel.channelCode}")
                                                                 @PathVariable("channelcode") String channelCode) {
        log.trace("getChannelPaymentTypes start");
        String uuid = UUID.randomUUID().toString();
        log.debug("getChannelPaymentTypes channelCode = {}, uuid {}", channelCode, uuid);
        PspChannelPaymentTypes response = apiConfigService.getChannelPaymentTypes(channelCode, uuid);
        PspChannelPaymentTypesResource resource = ChannelMapper.toResource(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getChannelPaymentTypes result = {}", resource);
        log.trace("getChannelPaymentTypes end");

        return resource;
    }

    @DeleteMapping(value = "psp/{channelcode}/{pspcode}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.deletePaymentServiceProvidersChannels}")
    public void deletePaymentServiceProvidersChannels(@ApiParam("${swagger.model.channel.channelCode}")
                                                      @PathVariable("channelcode") String channelCode,
                                                      @ApiParam("${swagger.request.pspCode}")
                                                      @PathVariable("pspcode") String pspCode) {
        log.trace("deletePaymentServiceProvidersChannels start");
        String uuid = UUID.randomUUID().toString();
        log.debug("deletePaymentServiceProvidersChannels code pspCode = {}, channel = {}, uuid {}", pspCode, channelCode, uuid);
        apiConfigService.deletePaymentServiceProvidersChannels(pspCode, channelCode, uuid);
        log.trace("deleteChannelPaymentType end");
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
        log.trace("updatePaymentServiceProvidersChannels start");
        String uuid = UUID.randomUUID().toString();
        log.debug("updatePaymentServiceProvidersChannels code pspCode = {}, channel = {}, pspChannelPaymentTypes = {}, uuid {}", pspCode, channelCode, pspChannelPaymentTypes, uuid);
        PspChannelPaymentTypes response = apiConfigService.updatePaymentServiceProvidersChannels(pspCode, channelCode, pspChannelPaymentTypes, uuid);
        PspChannelPaymentTypesResource resource = ChannelMapper.toResource(response);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "updatePaymentServiceProvidersChannels result = {}", resource);
        log.trace("updatePaymentServiceProvidersChannels end");
        return resource;
    }

    @DeleteMapping(value = "/{channelcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.deleteChannel}")
    public void deleteChannel(@ApiParam("${swagger.request.channelcode}")
                              @PathVariable("channelcode") String channelcode) {
        log.trace("deleteChannel start");
        String uuid = UUID.randomUUID().toString();
        log.debug("deleteChannel channelcode = {}, uuid = {}", channelcode, uuid);
        apiConfigService.deleteChannel(channelcode, uuid);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "deleteChannel with channelcode = {}", channelcode);
        log.trace("deleteChannel end");
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
        log.trace("getPspBrokerPsp start");
        String uuid = UUID.randomUUID().toString();
        log.debug("getPspBrokerPsp brokerPspCode = {} page = {} limit = {}, uuid {}", brokerPspCode, page, limit, uuid);
        PaymentServiceProviders response = apiConfigService.getPspBrokerPsp(limit, page, brokerPspCode, uuid);
        PaymentServiceProvidersResource resource = ChannelMapper.toResource(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getPspBrokerPsp result = {}", resource);
        log.trace("getPspBrokerPsp end");

        return resource;
    }


    @GetMapping(value = "/csv", produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannelsCSV}")
    public Resource getChannelsCSV(HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"channels.csv\"");
        log.trace(" getChannelsCSV start");
        String uuid = UUID.randomUUID().toString();
        log.debug("uuid = {}", uuid);
        Resource resource = apiConfigService.getChannelsCSV(uuid);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getChannelsCSV result = {}", resource);
        log.trace("getChannelDetails end");
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
        log.trace("getChannelPaymentServiceProviders start");
        String uuid = UUID.randomUUID().toString();
        log.debug("getChannelPaymentServiceProviders channelCode = {} page = {} limit = {}, uuid {}", channelCode, page, limit, uuid);
        ChannelPspList response = apiConfigService.getChannelPaymentServiceProviders(limit, page, channelCode, uuid);
        ChannelPspListResource resource = ChannelMapper.toResource(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getChannelPaymentServiceProviders result = {}", resource);
        log.trace("getChannelPaymentServiceProviders end");
        return resource;
    }

    @PostMapping(value = "/brokerspsp", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createBrokerPsp}")
    public BrokerPspDetailsResource createBrokerPsp(@RequestBody @NotNull BrokerPspDetailsDto brokerPspDetailsDto) {
        log.trace("createBrokerPsp start");
        String uuid = UUID.randomUUID().toString();
        log.debug("createBrokerPsp code brokerPspDetailsDto = {}", brokerPspDetailsDto);


        BrokerPspDetails brokerPspDetails = ChannelMapper.fromBrokerPspDetailsDto(brokerPspDetailsDto);
        BrokerPspDetails response = apiConfigService.createBrokerPsp(brokerPspDetails, uuid);


        BrokerPspDetailsResource resource = ChannelMapper.toResource(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createBrokerPsp result = {}", resource);
        log.trace("createBrokerPsp end");
        return resource;
    }

    @PostMapping(value = "/psp", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createPaymentServiceProvider}")
    public PaymentServiceProviderDetailsResource createPaymentServiceProvider(@RequestBody @NotNull PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto) {
        log.trace("createPaymentServiceProvider start");
        String uuid = UUID.randomUUID().toString();
        log.debug("createPaymentServiceProvider code paymentServiceProviderDto = {}", paymentServiceProviderDetailsDto);

        PaymentServiceProviderDetails paymentServiceProviderDetails = ChannelMapper.fromPaymentServiceProviderDetailsDto(paymentServiceProviderDetailsDto);
        PaymentServiceProviderDetails response = apiConfigService.createPaymentServiceProvider(paymentServiceProviderDetails, uuid);


        PaymentServiceProviderDetailsResource resource = ChannelMapper.toResource(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createPaymentServiceProvider result = {}", resource);
        log.trace("createPaymentServiceProvider end");
        return resource;
    }

    @PostMapping(value = "/pspdirect", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createPaymentServiceProvider}")
    public PaymentServiceProviderDetailsResource createPSPDirect(@RequestBody @NotNull PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto) {
        log.trace("createPSPDirect start");
        String uuid = UUID.randomUUID().toString();
        log.debug("createPSPDirect code paymentServiceProviderDto = {}", paymentServiceProviderDetailsDto);

        Map<String, Object> res = ChannelMapper.fromPaymentServiceProviderDetailsDtoToMap(paymentServiceProviderDetailsDto);
        BrokerPspDetails brokerPspDetails = (BrokerPspDetails) res.get("broker");
        PaymentServiceProviderDetails paymentServiceProviderDetails = (PaymentServiceProviderDetails) res.get("psp");

        apiConfigService.createBrokerPsp(brokerPspDetails, uuid);
        PaymentServiceProviderDetails responsePSP = apiConfigService.createPaymentServiceProvider(paymentServiceProviderDetails, uuid);


        PaymentServiceProviderDetailsResource resource = ChannelMapper.toResource(responsePSP);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createPSPDirect result = {}", resource);
        log.trace("createPSPDirect end");
        return resource;
    }

    @GetMapping(value = "/{pspcode}/generate", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannelCode}")
    public ChannelCodeResource getChannelCode(@ApiParam("${swagger.request.pspCode}")
                                              @PathVariable("pspcode") String pspCode) {
        log.trace("getChannelCode start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getChannelCode pspcode = {}, xRequestId = {}", pspCode, xRequestId);
        String result = apiConfigService.generateChannelCode(pspCode, xRequestId);
        ChannelCodeResource channelCode = new ChannelCodeResource(result);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getChannelCode result = {}", channelCode);
        log.trace("getChannelCode end");
        return channelCode;
    }

    @GetMapping(value = "/{pspcode}/generateV2", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getChannelCode}")
    public ChannelCodeResource getChannelCodeV2(@ApiParam("${swagger.request.pspCode}")
                                                @PathVariable("pspcode") String pspCode) {
        log.trace("getChannelCodeV2 start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getChannelCodeV2 pspcode = {}, xRequestId = {}", pspCode, xRequestId);

        Channels channels = apiConfigService.getChannels(100, 0, pspCode, null, "ASC", xRequestId);
        WrapperChannels responseApiConfig = ChannelMapper.toWrapperChannels(channels);
        WrapperEntitiesList mongoList = wrapperService.findByIdLikeOrTypeOrBrokerCode(pspCode, WrapperType.CHANNEL, null, 0, 100);
        WrapperChannels responseMongo = ChannelMapper.toWrapperChannels(mongoList);
        WrapperChannels channelsMergedAndSorted = apiConfigService.mergeAndSortWrapperChannels(responseApiConfig, responseMongo, "ASC");
        String result = apiConfigService.generateChannelCodeV2(channelsMergedAndSorted.getChannelList(), pspCode, xRequestId);

        ChannelCodeResource channelCode = new ChannelCodeResource(result);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getChannelCode result = {}", channelCode);
        log.trace("getChannelCodeV2 end");
        return channelCode;
    }

    @GetMapping(value = "/psp/{pspcode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getPSPDetails}")
    public PaymentServiceProviderDetailsResource getPSPDetails(@ApiParam("${swagger.request.pspCode}")
                                                               @PathVariable("pspcode") String pspCode) {
        log.trace("getPSPDetails start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getPSPDetails pspcode = {}, xRequestId = {}", pspCode, xRequestId);
        PaymentServiceProviderDetails paymentServiceProviderDetails = apiConfigService.getPSPDetails(pspCode, xRequestId);
        PaymentServiceProviderDetailsResource resource = ChannelMapper.toResource(paymentServiceProviderDetails);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getPSPDetails result = {}", resource);
        log.trace("getPSPDetails end");
        return resource;
    }


    @PostMapping(value = "/create-wrapperChannel", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createWrapperChannelDetails}")
    public WrapperEntitiesOperations createWrapperChannelDetails(@RequestBody
                                                                 @Valid
                                                                 WrapperChannelDetailsDto wrapperChannelDetailsDto) {
        log.trace("createWrapperChannelDetails start");
        final String CREATE_CHANNEL_SUMMARY = "Validazione canale creazione: %s";
        final String CREATE_CHANEL_DESCRIPTION = "Il canale %s deve essere validato: %s";
        log.debug("createWrapperChannelDetails channelDetailsDto = {}", wrapperChannelDetailsDto);
        WrapperEntitiesOperations createdWrapperEntities = wrapperService.
                createWrapperChannelDetails(ChannelMapper.
                        fromWrapperChannelDetailsDto(wrapperChannelDetailsDto), wrapperChannelDetailsDto.getNote(), wrapperChannelDetailsDto.getStatus().name());
        log.debug("createWrapperChannelDetails result = {}", createdWrapperEntities);
        jiraServiceManagerService.createTicket(String.format(CREATE_CHANNEL_SUMMARY, wrapperChannelDetailsDto.getChannelCode()),
                String.format(CREATE_CHANEL_DESCRIPTION, wrapperChannelDetailsDto.getChannelCode(), wrapperChannelDetailsDto.getValidationUrl()));
        log.trace("createWrapperChannelDetails end");
        return createdWrapperEntities;
    }

    @PutMapping(value = "/update-wrapperChannel", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.updateWrapperChannelDetails}")
    public WrapperEntitiesOperations updateWrapperChannelDetails(@RequestBody
                                                                 @Valid
                                                                 ChannelDetailsDto channelDetailsDto) {
        log.trace("updateWrapperChannelDetails start");
        final String CREATE_CHANNEL_SUMMARY = "Validate channel update: %s";
        final String CREATE_CHANEL_DESCRIPTION = "The channel %s updated by broker %s needs to be validated: %s";
        log.debug("updateWrapperChannelDetails channelDetailsDto = {}", channelDetailsDto);
        WrapperEntitiesOperations createdWrapperEntities = wrapperService.
                updateWrapperChannelDetails(ChannelMapper.
                        fromChannelDetailsDto(channelDetailsDto), channelDetailsDto.getNote(), channelDetailsDto.getStatus().name(), null);
        log.debug("updateWrapperChannelDetails result = {}", createdWrapperEntities);
        jiraServiceManagerService.createTicket(String.format(CREATE_CHANNEL_SUMMARY, channelDetailsDto.getChannelCode()),
                String.format(CREATE_CHANEL_DESCRIPTION, channelDetailsDto.getChannelCode(), channelDetailsDto.getBrokerPspCode(), channelDetailsDto.getValidationUrl()));
        log.trace("updateWrapperChannelDetails end");
        return createdWrapperEntities;
    }

    @GetMapping(value = "/get-wrapperEntities/{code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getWrapperEntities}")
    public WrapperEntitiesOperations getWrapperEntities(@ApiParam("${swagger.request.code}") @PathVariable("code") String code) {
        log.trace("getWrapperEntities start");
        log.debug("getWrapperEntities cCode = {}", code);
        WrapperEntitiesOperations result = wrapperService.findById(code);
        log.debug("getWrapperEntities result = {}", result);
        log.trace("getWrapperEntities end");
        return result;
    }

    @PutMapping(value = "/update-wrapperChannelByOpt", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.updateWrapperChannelDetailsByOpt}")
    public WrapperEntitiesOperations updateWrapperChannelDetailsByOpt(@RequestBody
                                                                      @Valid
                                                                      ChannelDetailsDto channelDetailsDto) {
        log.trace("updateWrapperChannelDetailsByOpt start");
        log.debug("updateWrapperChannelDetailsByOpt channelDetailsDto = {}", channelDetailsDto);
        WrapperEntitiesOperations createdWrapperEntities = wrapperService.
                updateWrapperChannelDetailsByOpt(ChannelMapper.
                        fromChannelDetailsDto(channelDetailsDto), channelDetailsDto.getNote(), channelDetailsDto.getStatus().name());
        log.debug("updateWrapperChannelDetailsByOpt result = {}", createdWrapperEntities);
        log.trace("updateWrapperChannelDetailsByOpt end");
        return createdWrapperEntities;
    }

    @GetMapping(value = "/wfespplugins", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.updateWrapperChannelDetailsByOpt}")
    public ResponseEntity<WfespPluginConfs> getWfespPlugins() {
        log.trace("getWfespPlugins start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getWfespPlugins xRequestId = {}", xRequestId);
        WfespPluginConfs wfespPluginConfs = apiConfigService.getWfespPlugins(xRequestId);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getWfespPlugins result = {}", wfespPluginConfs);
        log.trace("getWfespPlugins end");
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
        log.trace("getWrapperByTypeAndStatus start");
        log.debug("getWrapperByTypeAndStatus wrapperType = {} WrapperStatus = {} page = {} limit = {}", wrapperType, wrapperStatus, page, limit);
        WrapperEntitiesList response = wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(wrapperStatus, wrapperType, brokerCode, idLike, page, limit, sorting);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getWrapperByTypeAndStatus result = {}", response);
        log.trace("getWrapperByTypeAndStatus end");

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
        log.trace("getAllChannelsMerged start");
        log.debug("getAllChannelsMerged page = {} limit = {}", page, limit);
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getchannels xRequestId = {}", xRequestId);

        Channels channels = apiConfigService.getChannels(limit, page, channelcode, brokerCode, sorting, xRequestId);
        WrapperChannels responseApiConfig = ChannelMapper.toWrapperChannels(channels);
        WrapperEntitiesList mongoList = wrapperService.findByIdLikeOrTypeOrBrokerCode(channelcode, WrapperType.CHANNEL, null, page, limit);

        WrapperChannels responseMongo = ChannelMapper.toWrapperChannels(mongoList);
        WrapperChannels channelsMergedAndSorted = apiConfigService.mergeAndSortWrapperChannels(responseApiConfig, responseMongo, sorting);
        WrapperChannelsResource response = ChannelMapper.toWrapperChannelsResource(channelsMergedAndSorted);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getAllChannelsMerged result = {}", response);
        log.trace("getAllChannelsMerged end");

        return response;
    }

    @GetMapping(value = "{brokerId}/channels", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getChannelDetailsListByBroker}")
    public ChannelDetailsResourceList getChannelDetailsListByBroker(@PathVariable("brokerId") String brokerId,
                                                                    @RequestParam(required = false) String channelId,
                                                                    @RequestParam(required = false, defaultValue = "10") Integer limit,
                                                                    @RequestParam(required = false, defaultValue = "0") Integer page) {
        log.trace("getChannelDetailsListByBroker start");
        log.debug("getChannelDetailsListByBroker page = {} limit = {}", page, limit);
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getChannelDetailsListByBroker xRequestId = {}", xRequestId);
        ChannelDetailsList response = apiConfigSelfcareIntegrationService.getChannelsDetailsListByBroker(brokerId, channelId, limit, page, xRequestId);
        ChannelDetailsResourceList resource = ChannelMapper.fromChannelDetailsList(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getStationsDetailsListByBroker result = {}", resource);
        log.trace("getStationsDetailsListByBroker end");
        return resource;
    }


    @GetMapping(value = "/brokerspsp", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.createBrokerPsp}")
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
        log.trace("getBrokersPsp start");
        String uuid = UUID.randomUUID().toString();
        log.debug("getBrokersPsp limit = {}, page = {},filterByCode = {}, filterByName = {}, orderBy = {}, sorting = {}", limit, page, filterByCode, filterByName, orderBy, sorting);


        BrokersPsp response = apiConfigService.getBrokersPsp(limit, page, filterByCode, filterByName, orderBy, sorting, uuid);


        BrokersPspResource resource = ChannelMapper.toResource(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getBrokersPsp result = {}", resource);
        log.trace("getBrokersPsp end");
        return resource;
    }

    @GetMapping(value = "/brokerdetails", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getBrokerPsp}")
    public BrokerPspDetailsResource getBrokerPsp(@ApiParam("swagger.request.brokerpspcode")
                                                 @RequestParam(required = false, name = "brokerpspcode") String brokerPspCode) {
        log.trace("getBrokerPsp start");
        String uuid = UUID.randomUUID().toString();
        log.debug("getBrokerPsp brokerPspCode = {} , xRequestId:  {}", brokerPspCode, uuid);

        BrokerPspDetails response = apiConfigService.getBrokerPsp(brokerPspCode, uuid);

        BrokerPspDetailsResource resource = ChannelMapper.toResource(response);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getBrokerPsp result = {}", resource);
        log.trace("getBrokerPsp end");

        return resource;
    }

//    @GetMapping(value = "sendEmail")
//    @ResponseStatus(HttpStatus.OK)
//    public String sendMail(@RequestParam String to,
//                           @RequestParam String subject,
//                           @RequestParam String body
//    ) {
//        log.info("Sending mail");
//        final String CREATE_CHANEL_SUBJECT = "Creazione Canale";
//        final String CREATE_CHANEL_EMAIL_BODY = String.format("Buongiorno\n\nIl canale %s è stato validato da un operatore e risulta essere attivo\n\nSaluti", "12232345657678878");
//        ChannelDetailsDto channelDetailsDto = new ChannelDetailsDto();
//        channelDetailsDto.setPspEmail("a@s.it");
//        String email = channelDetailsDto.getEmail();
//        String res = awsSesService.sendEmail(to, CREATE_CHANEL_SUBJECT, CREATE_CHANEL_EMAIL_BODY);
//        return res;
//    }
}