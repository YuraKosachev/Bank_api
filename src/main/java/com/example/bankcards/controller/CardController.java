package com.example.bankcards.controller;

import com.example.bankcards.constants.ApiConstants;
import com.example.bankcards.constants.SecurityConstants;
import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.Role;
import com.example.bankcards.enums.TokenStatus;
import com.example.bankcards.exception.AccessException;
import com.example.bankcards.interfaces.services.AccountService;
import com.example.bankcards.interfaces.services.CardService;
import com.example.bankcards.interfaces.services.JwtService;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.repository.specifications.FilterSpecification;
import com.example.bankcards.util.PermissionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.API_PREFIX_V1)
@Tag(name = ApiConstants.Card.API_CARD_CONTROLLER_NAME)
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    private final CardMapper cardMapper;

    @PostMapping(ApiConstants.Card.API_CARD_CREATE)
    @Operation(
            summary = "Create a new payment card",
            description = """
                    Creates a payment card linked to a bank account.
                    The card number is securely encrypted before being stored.
                    Only authorized users with proper permissions can perform this operation.
                    """
    )
    @ApiResponse(description = "Card successfully created", responseCode = "200",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CardDto.class))})
    @ApiResponse(description = "Error", responseCode = "400",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<CardDto> create(@RequestBody @Valid CardCreateDto cardDto) {
        return ResponseEntity.ok(cardService.create(cardDto, cardMapper::toDto));
    }

    @GetMapping(ApiConstants.Card.API_CARD_LIST)

    @Operation(summary = "Get a paginated list of cards with filters and sorting",
            description = """
                    Returns a list of cards with optional filters for balance range and owner's name.
                    Supports pagination and sorting by any available field in ascending or descending order.
                    """)
    @ApiResponse(description = "Success", responseCode = "200",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))})
    @ApiResponse(description = "Error", responseCode = "400",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<Page<CardDto>> getAll(
            UsernamePasswordAuthenticationToken token,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "balanceGreaterThanOrEqual", required = false) Double minBalance,
            @RequestParam(value = "balanceLessThanOrEqual", required = false) Double maxBalance,
            @RequestParam(name = "ownerLike", required = false) String ownerLike,
            @RequestParam(name = "direction", required = false) String direction,
            @RequestParam(name = "field", required = false) String field) {

        Sort sort = Sort.unsorted();
        if (direction != null && field != null) {
            sort = Sort.by(Sort.Direction.fromString(direction), field);
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Card> spec = Specification.allOf();

        //if user in user role -> get info about his cards, if admin -> get all cards
        if (PermissionUtils.inRole(token, Role.USER)) {
            var accId = PermissionUtils.getAccountId(token);
            spec = spec.and(FilterSpecification.equal("account.id", accId));
        }

        if (minBalance != null) {
            spec = spec.and(FilterSpecification.greaterThanOrEqual("balance", BigDecimal.valueOf(minBalance), BigDecimal.class));
        }
        if (maxBalance != null) {
            spec = spec.and(FilterSpecification.lessThanOrEqual("balance", BigDecimal.valueOf(maxBalance), BigDecimal.class));
        }
        if (StringUtils.hasText(ownerLike)) {
            spec = spec.and(FilterSpecification.like("owner", ownerLike));
        }

        return ResponseEntity.ok(cardService.getPages(spec, pageable, cardMapper::toDto));
    }

    @PutMapping(ApiConstants.Card.API_CARD_REQUEST_BLOCK)
    @Operation(
            summary = "Request to block a payment card",
            description = """
                    Sends a request to change the status of a specific payment card to 'BLOCKED'. 
                    The card must belong to the authenticated account and must currently be in an active state. 
                    This action is typically used when a card is lost, stolen, or suspected of unauthorized use. 
                    The operation is validated and persisted in the system for further processing or immediate effect.
                    """
    )

    @ApiResponse(description = "Success", responseCode = "204")
    @ApiResponse(description = "Error", responseCode = "400",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<?> setBlockedRequest(UsernamePasswordAuthenticationToken token,
                                               @RequestBody @Valid CardBlockedRequestDto cardBlockedRequestDto) {
        var accId = PermissionUtils.getAccountId(token);
        cardService.setBlockRequest(accId, cardBlockedRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(ApiConstants.Card.API_CARD_STATUS)
    @Operation(summary = "Change the status of a payment card",
            description = """
                    Updates the status of a specific payment card, such as setting it to ACTIVE or BLOCKED.
                    This endpoint is typically used to activate newly issued cards or block cards due to fraud or user request.
                    The card ID and the new status must be provided.
                    """)
    @ApiResponse(description = "Success", responseCode = "204")
    @ApiResponse(description = "Error", responseCode = "400",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<?> setStatus(@RequestBody @Valid CardStatusDto cardStatusDto) {
        cardService.changeCardStatus(cardStatusDto.cardId(), cardStatusDto.status());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiConstants.Card.API_CARD_BY_ID)
    @Operation(
            summary = "Retrieve payment card details",
            description = """
        Returns detailed information about a payment card by its ID.
        Users with role USER can access only their own cards.
        Users with role ADMIN can view cards belonging to any user.
        """
    )
    @ApiResponse(description = "Success", responseCode = "200",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CardDto.class))})
    @ApiResponse(description = "Error", responseCode = "400",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<CardDto> getItem(UsernamePasswordAuthenticationToken token, @RequestParam("id") UUID id) {
        var accId = PermissionUtils.getAccountId(token);
        return ResponseEntity.ok(cardService.findById(accId, id, cardMapper::toDto));
    }

    @PutMapping(ApiConstants.Card.API_CARD_TRANSFER)
    @Operation(summary = "Transfer funds between your own cards",
            description = """
                    Allows transferring a specified amount from one active card to another active card within the same account.
                    Validates card ownership, status, and balance before performing the transfer.
                    The operation is transactional to ensure data integrity.
                    """)
    @ApiResponse(description = "Success", responseCode = "204")
    @ApiResponse(description = "Error", responseCode = "400",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<?> transfer(
            UsernamePasswordAuthenticationToken token,
            @RequestBody @Valid CardTransferDto cardTransferDto) {
        cardService.transfer(PermissionUtils.getAccountId(token), cardTransferDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(ApiConstants.Card.API_CARD_BY_ID)
    @Operation(
            summary = "Delete a payment card",
            description = """
        Permanently deletes a payment card by its ID.
        This operation is restricted to users with the ADMIN role only.
        Regular users (USER role) are not authorized to perform this action.
        """
    )
    @ApiResponse(description = "Success", responseCode = "200")
    @ApiResponse(description = "Error", responseCode = "400",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<?> delete(@RequestParam("id") UUID id) {
        cardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}