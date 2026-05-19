package br.com.bank.bankapi.transaction.controller;

import br.com.bank.bankapi.exception.response.ApiError;
import br.com.bank.bankapi.transaction.dto.DepositDTO;
import br.com.bank.bankapi.transaction.dto.TransactionResponseDTO;
import br.com.bank.bankapi.transaction.dto.TransferDTO;
import br.com.bank.bankapi.transaction.dto.WithdrawDTO;
import br.com.bank.bankapi.transaction.service.TransactionService;
import br.com.bank.bankapi.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transaction", description = "Endpoints for deposit, withdrawal, transfer and transaction history.")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(
            summary = "Deposit into an account",
            description = "Performs a deposit into an active account owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deposit completed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Account is inactive or amount is invalid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(
            @RequestBody(
                    description = "Deposit data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DepositDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody DepositDTO dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("Deposit requested. userId={} accountId={} amount={}",
                user.getId(), dto.accountId(), dto.amount());

        TransactionResponseDTO response = transactionService.deposit(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Withdraw from an account",
            description = "Performs a withdrawal from an active account owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Withdrawal completed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Insufficient funds, inactive account, or invalid amount",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(
            @RequestBody(
                    description = "Withdrawal data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = WithdrawDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody WithdrawDTO dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("Withdrawal requested. userId={} accountId={} amount={}",
                user.getId(), dto.accountId(), dto.amount());

        TransactionResponseDTO response = transactionService.withdraw(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Transfer to another account",
            description = "Transfers money from an active source account owned by the authenticated user to another active account identified by agency and account number."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transfer completed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Source or destination account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Insufficient funds, inactive account, same source and destination account, or invalid amount",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(
            @RequestBody(
                    description = "Transfer data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransferDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody TransferDTO dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("Transfer requested. userId={} sourceAccountId={} destinationAgency={} destinationAccountNumber={} amount={}",
                user.getId(),
                dto.sourceAccountId(),
                dto.destinationAgency(),
                dto.destinationAccountNumber(),
                dto.amount());

        TransactionResponseDTO response = transactionService.transfer(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Find transaction by id",
            description = "Returns a transaction by its identifier, as long as it belongs to an account owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Transaction found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid transaction id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "Transaction not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getById(
            @Parameter(description = "Transaction identifier", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        log.info("Transaction lookup requested. userId={} transactionId={}", user.getId(), id);

        TransactionResponseDTO response = transactionService.getById(id, user);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List transactions by account",
            description = "Returns the paginated transaction history for an account owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Transactions returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid account id or pagination parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "Account not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Page<TransactionResponseDTO>> listByAccount(
            @Parameter(description = "Account identifier", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID accountId,
            @Parameter(description = "Pagination configuration")
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal User user
    ) {
        log.info("Transaction history requested. userId={} accountId={} page={} size={}",
                user.getId(), accountId, pageable.getPageNumber(), pageable.getPageSize());

        Page<TransactionResponseDTO> response = transactionService.listByAccount(accountId, pageable, user);
        return ResponseEntity.ok(response);
    }
}