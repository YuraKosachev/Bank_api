package com.example.bankcards.controller;

import com.example.bankcards.constants.ApiConstants;
import com.example.bankcards.constants.SecurityConstants;
import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Account;
import com.example.bankcards.enums.Role;
import com.example.bankcards.exception.AccessException;
import com.example.bankcards.interfaces.services.AccountService;
import com.example.bankcards.mappers.AccountMapper;
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

import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.API_PREFIX_V1)
@Tag(name = ApiConstants.Account.API_ACCOUNT_CONTROLLER_NAME)
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @GetMapping(ApiConstants.Account.API_ACCOUNT_LIST)
    @Operation(description = "Endpoint to get list of account",
            summary = "This is a summary for account post endpoint")
    @ApiResponse(description = "Success", responseCode = "200",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))})
    @ApiResponse(description = "Error", responseCode = "400",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<Page<AccountDto>> getList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                                    @RequestParam(name = "search", required = false) String search,
                                                    @RequestParam(name = "direction", required = false) String direction,
                                                    @RequestParam(name = "field", required = false) String field) {

        Sort sort = Sort.unsorted();
        if (direction != null && field != null) {
            sort = Sort.by(Sort.Direction.fromString(direction), field);
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Account> spec = Specification.allOf();
        if(StringUtils.hasText(search)) {
            spec = spec.and(FilterSpecification.like("username", search))
                    .or(FilterSpecification.like("mail", search))
                    .or(FilterSpecification.like("firstName", search))
                    .or(FilterSpecification.like("lastName", search));
        }

        return ResponseEntity.ok(accountService.getPages(spec, pageable, accountMapper::entityToDto));
    }

    @GetMapping(ApiConstants.Account.API_ACCOUNT_BY_ID)
    @Operation(description = "Endpoint to create new account",
            summary = "This is a summary for account post endpoint")
    @ApiResponse(description = "Success", responseCode = "200",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccountDto.class))})
    @ApiResponse(description = "Error", responseCode = "400",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<AccountDto> getItem(UsernamePasswordAuthenticationToken token, @RequestParam("id") UUID id) {
        if(!PermissionUtils.inAdminRoleOrOwner(token, id))
            throw new AccessException("You do not have permission to get info about this account");
        return ResponseEntity.ok(accountService.findById(id, (acc) -> accountMapper.entityToDto(acc)));
    }

    @PostMapping(ApiConstants.Account.API_ACCOUNT_CREATE_UPDATE)
    @Operation(description = "Endpoint to create new account",
            summary = "This is a summary for account post endpoint")
    @ApiResponse(description = "Success", responseCode = "200",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccountDto.class))})
    @ApiResponse(description = "Error", responseCode = "400",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    public ResponseEntity<AccountDto> create(@RequestBody @Valid AccountCreateDto account) {
        return ResponseEntity.ok(accountService.create(account, (acc) -> accountMapper.entityToDto(acc)));
    }

    @PutMapping(ApiConstants.Account.API_ACCOUNT_CREATE_UPDATE)
    @Operation(description = "Endpoint to update account",
            summary = "This is a summary for account post endpoint")
    @ApiResponse(description = "Success", responseCode = "200",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccountDto.class))})
    @ApiResponse(description = "Error", responseCode = "400",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<AccountDto> update(UsernamePasswordAuthenticationToken token, @RequestBody @Valid AccountUpdateDto account) {
        if(!PermissionUtils.inAdminRoleOrOwner(token,account.id()))
            throw new AccessException("You do not have permission to update this account");
        return ResponseEntity.ok(accountService.update(account, (acc) -> accountMapper.entityToDto(acc)));
    }

    @PutMapping(ApiConstants.Account.API_ACCOUNT_ROLE_UPDATE)
    @Operation(description = "Endpoint to update account",
            summary = "This is a summary for account post endpoint")
    @ApiResponse(description = "Success", responseCode = "200")
    @ApiResponse(description = "Error", responseCode = "400",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<?> updateRole(UsernamePasswordAuthenticationToken token, @RequestBody @Valid RoleUpdateDto account) {
        if(!PermissionUtils.inAdminRoleOrOwner(token,account.accountId()))
            throw new AccessException("You do not have permission to update this account");
        accountService.updateRole(account);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping (ApiConstants.Account.API_ACCOUNT_BY_ID)
    @Operation(description = "Endpoint to delete account",
            summary = "This is a summary for account post endpoint")
    @ApiResponse(description = "Success", responseCode = "200")
    @ApiResponse(description = "Error", responseCode = "400",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<?> delete(UsernamePasswordAuthenticationToken token, @RequestParam("id") UUID id) {
        if(!PermissionUtils.inAdminRoleOrOwner(token,id))
            throw new AccessException("You do not have permission to delete this account");

        accountService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}