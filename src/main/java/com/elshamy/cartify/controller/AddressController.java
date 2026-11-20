package com.elshamy.cartify.controller;

import com.elshamy.cartify.dtos.AddressDTO;
import com.elshamy.cartify.model.User;
import com.elshamy.cartify.service.address.IAddressService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final IAddressService addressService;

    @PostMapping
    public ResponseEntity<AddressDTO.AddressResponse> addAddress(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody AddressDTO.CreateAddressRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.addAddress(currentUser.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<AddressDTO.AddressResponse>> getUserAddresses(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(addressService.getUserAddresses(currentUser.getId()));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDTO.AddressResponse> getAddressById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @Positive Long addressId
    ) {
        return ResponseEntity.ok(addressService.getAddressById(currentUser.getId(), addressId));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDTO.AddressResponse> updateAddress(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @Positive Long addressId,
            @Valid @RequestBody AddressDTO.UpdateAddressRequest request
    ) {
        return ResponseEntity.ok(addressService.updateAddress(currentUser.getId(), addressId, request));
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressDTO.AddressResponse> setDefaultAddress(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @Positive Long addressId
    ) {
        return ResponseEntity.ok(addressService.setDefaultAddress(currentUser.getId(), addressId));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @Positive Long addressId
    ) {
        addressService.deleteAddress(currentUser.getId(), addressId);
        return ResponseEntity.noContent().build();
    }
}