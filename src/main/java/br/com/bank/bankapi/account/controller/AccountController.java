package br.com.bank.bankapi.account.controller;

import br.com.bank.bankapi.account.dto.AccountResponseDTO;
import br.com.bank.bankapi.account.dto.CreateAccountDTO;
import br.com.bank.bankapi.account.dto.UpdateAccountStatusDTO;
import br.com.bank.bankapi.account.service.AccountService;
import br.com.bank.bankapi.exception.response.ApiError;
import br.com.bank.bankapi.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@Tag(
        name = "Account",
        description = "Endpoints for account creation, retrieval, listing, and status management."
)
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Create account",
            description = "Creates a new bank account for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Account successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDTO.class)
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
                    description = "Related resource not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Account already exists or conflicts with current state",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<AccountResponseDTO> create(
            @Valid @RequestBody CreateAccountDTO dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("Account creation requested. userId={} agency={} number={} type={}",
                user.getId(), dto.agency(), dto.number(), dto.type());

        AccountResponseDTO created = accountService.create(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Get account by id",
            description = "Returns the details of a specific account belonging to the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDTO.class)
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
                    description = "Account not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        log.info("Account fetch requested. accountId={} userId={}", id, user.getId());

        AccountResponseDTO response = accountService.getById(id, user);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List my accounts",
            description = "Returns all accounts associated with the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Accounts listed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = AccountResponseDTO.class)
                            )
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
            )
    })
    @GetMapping("/me")
    public ResponseEntity<List<AccountResponseDTO>> getMyAccounts(
            @AuthenticationPrincipal User user
    ) {
        log.info("Account listing requested for authenticated user. userId={}", user.getId());

        List<AccountResponseDTO> accounts = accountService.listMyAccounts(user);
        return ResponseEntity.ok(accounts);
    }

    @Operation(
            summary = "Update account status",
            description = "Updates the status of a specific account belonging to the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account status updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDTO.class)
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
                    description = "Account not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<AccountResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountStatusDTO dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("Account status update requested. accountId={} userId={} newStatus={}",
                id, user.getId(), dto.status());

        AccountResponseDTO updated = accountService.updateStatus(id, dto, user);
        return ResponseEntity.ok(updated);
    }
}
