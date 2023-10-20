package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;
import it.pagopa.selfcare.pagopa.backoffice.core.TavoloOpService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.TavoloOpMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloOp.TavoloOpDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/tavoloop", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "tavoloop")
public class TavoloOpController {

    private final TavoloOpService tavoloOpService;


    @Autowired
    public TavoloOpController(TavoloOpService tavoloOpService) {
        this.tavoloOpService = tavoloOpService;

    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.api.tavoloop.insert}")
    public TavoloOpOperations insert(@RequestBody TavoloOpDto dto) {
        TavoloOp tavoloOp = TavoloOpMapper.fromDto(dto);
        return tavoloOpService.insert(tavoloOp);

    }
}
