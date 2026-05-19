package br.com.bank.bankapi.customer.controller;

import br.com.bank.bankapi.customer.dto.CreateCustomerDTO;
import br.com.bank.bankapi.customer.dto.CustomerResponseDTO;
import br.com.bank.bankapi.customer.dto.UpdateCustomerDTO;
import br.com.bank.bankapi.customer.dto.UpdateCustomerStatusDTO;
import br.com.bank.bankapi.customer.service.CustomerService;
import br.com.bank.bankapi.exception.response.ApiError;
import br.com.bank.bankapi.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
@Tag(
        name = "Customer",
        description = "Endpoints for customer creation, retrieval, update, and status management."
)
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(
            summary = "Create customer",
            description = "Creates a new customer associated with the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Customer already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(
            @Valid @RequestBody CreateCustomerDTO dto,
            @AuthenticationPrincipal User user
    ) {

        log.info("Customer creation requested by userId={}", user.getId());

        CustomerResponseDTO created = customerService.create(dto, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Get customer by id",
            description = "Returns the details of a specific customer accessible to the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {

        log.info("GET /customers/{} requested by userId={}", id, user.getId());

        CustomerResponseDTO response = customerService.getById(id, user);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update my customer data",
            description = "Updates the data of the customer associated with the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @PatchMapping("/me")
    public ResponseEntity<CustomerResponseDTO> updateMe(
            @Valid @RequestBody UpdateCustomerDTO dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("PATCH /customers/me requested by userId={} fields=[fullName:{}]",
                user.getId(),
                dto.fullName() != null && !dto.fullName().isBlank()
        );

        return ResponseEntity.ok(customerService.updateMyCustomer(dto, user));
    }

    @Operation(
            summary = "Update customer status",
            description = "Updates the status of a specific customer accessible to the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer status updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerStatusDTO dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("PATCH /customers/{}/status requested by userId={} newStatus={}",
                id, user.getId(), dto.status());

        return ResponseEntity.ok(customerService.updateStatus(id, dto, user));
    }
}