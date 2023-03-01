package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationsResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "stations")
public class StationController {
    private final ApiConfigService apiConfigService;

    @Autowired
    public StationController(ApiConfigService apiConfigService) {
        this.apiConfigService = apiConfigService;
    }


    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.stations.getStations}")
    public StationsResource getStations(@ApiParam("${swagger.pageable.number}")
                                            @RequestParam(required = false, defaultValue = "50") Integer limit,
                                        @ApiParam("${swagger.pageable.start}")
                                            @RequestParam(required = true) Integer page,
                                        @ApiParam("${swagger.model.station.id}")
                                            @RequestParam(required = false) String stationCode,
                                        @ApiParam("${swagger.model.stations.ecCode}")
                                            @RequestParam(required = false) String creditorInstitutionCode,
                                        @ApiParam("${swagger.model.sort.order}")
                                            @RequestParam(required = false, name = "ordering", defaultValue = "DESC") String sort){

        return null;
    }

}
