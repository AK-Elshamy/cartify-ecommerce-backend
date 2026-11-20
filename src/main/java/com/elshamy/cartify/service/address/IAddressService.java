package com.elshamy.cartify.service.address;

import com.elshamy.cartify.dtos.AddressDTO;

import java.util.List;

public interface IAddressService {
    AddressDTO.AddressResponse addAddress(Long userId, AddressDTO.CreateAddressRequest request);

    List<AddressDTO.AddressResponse> getUserAddresses(Long userId);

    AddressDTO.AddressResponse getAddressById(Long userId, Long addressId);

    AddressDTO.AddressResponse updateAddress(Long userId, Long addressId, AddressDTO.UpdateAddressRequest request);

    AddressDTO.AddressResponse setDefaultAddress(Long userId, Long addressId);

    void deleteAddress(Long userId, Long addressId);
}