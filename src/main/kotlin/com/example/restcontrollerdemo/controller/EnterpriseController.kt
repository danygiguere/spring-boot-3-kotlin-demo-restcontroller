package com.example.restcontrollerdemo.controller

import com.example.restcontrollerdemo.dto.EnterpriseResponse
import com.example.restcontrollerdemo.security.AuthenticatedUser
import com.example.restcontrollerdemo.service.EnterpriseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/enterprises")
@Tag(name = "Enterprises")
class EnterpriseController(
    private val enterpriseService: EnterpriseService,
) {
    @GetMapping("/current")
    @Operation(operationId = "getCurrentEnterprise", summary = "Get the caller's enterprise")
    suspend fun current(me: AuthenticatedUser): EnterpriseResponse = enterpriseService.current(me)
}
