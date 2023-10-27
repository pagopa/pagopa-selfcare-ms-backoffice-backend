package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;
import it.pagopa.selfcare.pagopa.backoffice.core.TavoloOpService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.TavoloOpMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloop.TavoloOpDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloop.TavoloOpResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloop.TavoloOpResourceList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/tavolo-operativo", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Tavolo Operativo")
public class TavoloOpController {

    private final TavoloOpService tavoloOpService;


    @Autowired
    public TavoloOpController(TavoloOpService tavoloOpService) {
        this.tavoloOpService = tavoloOpService;

    }

    @GetMapping(value = "/{ecCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.tavoloOp.getTavoloOpDetails}")
    public TavoloOpResource getTavoloOpDetails(@ApiParam("${swagger.request.ecCode}")
                                               @PathVariable("ecCode") String ecCode) {

        TavoloOpOperations tavoloOpOperations = tavoloOpService.findByTaxCode(ecCode);
        return TavoloOpMapper.toResource(tavoloOpOperations);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.tavoloop.insert}")
    public TavoloOpOperations insert(@RequestBody TavoloOpDto dto) {
        TavoloOp tavoloOp = TavoloOpMapper.fromDto(dto);
        return tavoloOpService.insert(tavoloOp);

    }

    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.tavoloop.update}")
    public TavoloOpOperations update(@RequestBody TavoloOpDto dto) {
        TavoloOp tavoloOp = TavoloOpMapper.fromDto(dto);
        return tavoloOpService.update(tavoloOp);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.tavoloOp.getAllTavoloOpDetails}")
    public TavoloOpResourceList getAllTavoloOpDetails() {

        TavoloOpEntitiesList tavoloOpResourceList = tavoloOpService.findAll();
        return TavoloOpMapper.toResource(tavoloOpResourceList);
    }
}
