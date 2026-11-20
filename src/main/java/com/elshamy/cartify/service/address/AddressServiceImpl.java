package com.elshamy.cartify.service.address;

import com.elshamy.cartify.dtos.AddressDTO;
import com.elshamy.cartify.exception.ResourceNotFoundException;
import com.elshamy.cartify.model.Address;
import com.elshamy.cartify.model.User;
import com.elshamy.cartify.repository.AddressRepository;
import com.elshamy.cartify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements IAddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AddressDTO.AddressResponse addAddress(Long userId, AddressDTO.CreateAddressRequest request) {
        User user = findUserById(userId);
        boolean isFirstAddress = !addressRepository.existsByUserId(userId);
        boolean shouldBeDefault = isFirstAddress || Boolean.TRUE.equals(request.isDefault());

        if (shouldBeDefault) {
            unsetCurrentDefaultAddress(userId);
        }

        Address address = new Address();
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());
        address.setPhoneNumber(request.phoneNumber());
        address.setDefault(shouldBeDefault);

        user.addAddress(address);
        Address savedAddress = addressRepository.save(address);

        return AddressDTO.AddressResponse.from(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO.AddressResponse> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(AddressDTO.AddressResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO.AddressResponse getAddressById(Long userId, Long addressId) {
        Address address = findUserAddress(userId, addressId);
        return AddressDTO.AddressResponse.from(address);
    }

    @Override
    @Transactional
    public AddressDTO.AddressResponse updateAddress(
            Long userId,
            Long addressId,
            AddressDTO.UpdateAddressRequest request
    ) {
        Address address = findUserAddress(userId, addressId);

        if (Boolean.TRUE.equals(request.isDefault()) && !address.isDefault()) {
            unsetCurrentDefaultAddress(userId);
            address.setDefault(true);
        } else if (Boolean.FALSE.equals(request.isDefault())) {
            address.setDefault(false);
        }

        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());
        address.setPhoneNumber(request.phoneNumber());

        Address updatedAddress = addressRepository.save(address);
        return AddressDTO.AddressResponse.from(updatedAddress);
    }

    @Override
    @Transactional
    public AddressDTO.AddressResponse setDefaultAddress(Long userId, Long addressId) {
        Address address = findUserAddress(userId, addressId);

        if (!address.isDefault()) {
            unsetCurrentDefaultAddress(userId);
            address.setDefault(true);
            address = addressRepository.save(address);
        }

        return AddressDTO.AddressResponse.from(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = findUserAddress(userId, addressId);
        boolean wasDefault = address.isDefault();

        User user = address.getUser();
        user.removeAddress(address);
        addressRepository.delete(address);

        if (wasDefault) {
            addressRepository.findByUserId(userId).stream()
                    .findFirst()
                    .ifPresent(nextDefault -> {
                        nextDefault.setDefault(true);
                        addressRepository.save(nextDefault);
                    });
        }
    }


    private Address findUserAddress(Long userId, Long addressId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + addressId
                ));
    }

    private void unsetCurrentDefaultAddress(Long userId) {
        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(currentDefault -> {
                    currentDefault.setDefault(false);
                    addressRepository.save(currentDefault);
                });
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId
                ));
    }
}