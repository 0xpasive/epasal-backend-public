package epasal.com.userservice.services.impl;

import epasal.com.userservice.components.CurrentUser;
import epasal.com.userservice.dtos.requests.AddressExists;
import epasal.com.userservice.dtos.requests.CreateAddressRequest;
import epasal.com.userservice.dtos.requests.UpdateAddressRequest;
import epasal.com.userservice.dtos.response.AddressResponse;
import epasal.com.userservice.entity.Address;
import epasal.com.userservice.exception.ResourceNotFoundException;
import epasal.com.userservice.exception.UnauthorizedException;
import epasal.com.userservice.mapper.AddressMapper;
import epasal.com.userservice.repo.AddressRepository;
import epasal.com.userservice.repo.UserRepository;
import epasal.com.userservice.services.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final CurrentUser currentUser;
    private final UserRepository userRepository;

    @Override
    public AddressResponse addAddress(CreateAddressRequest createAddressRequest) {
        log.info("Adding address for user with id: {}", currentUser.getUserId());

        List<Address> address = addressRepository.findByUserId(currentUser.getUserId());

        if (address.size() == 2) {
            log.warn("User with id: {} has already added two addresses. Cannot add more.", currentUser.getUserId());
            throw new UnauthorizedException("You cannot add more than two addresses. Please edit or delete an existing address to add a new one.");
        }

        Address newaddress = addressMapper.toEntity(createAddressRequest);
        newaddress.setUserId(currentUser.getUserId());
        newaddress.setCreatedAt(LocalDateTime.now());
        newaddress.setUpdatedAt(LocalDateTime.now());

        if (address.isEmpty()) {
            newaddress.setPrimary(true);
            addressRepository.save(newaddress);
            log.info("Address added as Primary");

        } else {
            newaddress.setPrimary(false);
            addressRepository.save(newaddress);
            log.info("Address added as Secondary");
        }

        return addressMapper.toResponse(newaddress);
    }

    @Override
    public List<AddressResponse> getAddress() {
        log.info("Fetching Address for user with id: {}", currentUser.getUserId());
        return addressRepository.findAll()
                .stream()
                .filter(address -> address.getUserId().equals(currentUser.getUserId()))
                .map(addressMapper::toResponse)
                .toList();
    }

    @Override
    public AddressResponse getAddressById(String addressId) {
        log.info("Fetching Address with id: {} for user with id: {}", addressId, currentUser.getUserId());
        Address address = addressRepository.findById(UUID.fromString(addressId)).orElseThrow(() -> new ResourceNotFoundException("Address Not Found"));
        return addressMapper.toResponse(address);
    }

    @Override
    public AddressResponse editAddress(String addressId, UpdateAddressRequest updateAddressRequest) {
        log.info("Editing Address with id: {} for user with id: {}", addressId, currentUser.getUserId());

        Address address = addressRepository.findById(UUID.fromString(addressId)).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        log.debug("Address found: {}", address);
        if (!address.getUserId().equals(currentUser.getUserId())) {
            throw new ResourceNotFoundException("Address not found for this user");

        }
        log.debug("Updating address with new values: {}", updateAddressRequest);

        Optional.ofNullable(updateAddressRequest.getStreet()).ifPresent(address::setStreet);
        Optional.ofNullable(updateAddressRequest.getCity()).ifPresent(address::setCity);
        Optional.ofNullable(updateAddressRequest.getProvince()).ifPresent(address::setProvince);
        Optional.ofNullable(updateAddressRequest.getCountry()).ifPresent(address::setCountry);
        Optional.ofNullable(updateAddressRequest.getPostalCode()).ifPresent(address::setPostalCode);
        Optional.ofNullable(updateAddressRequest.getPhoneNumber()).ifPresent(address::setPhoneNumber);

        address.setUpdatedAt(LocalDateTime.now());

        addressRepository.save(address);
        log.info("Address with id: {} updated successfully for user with id: {}", addressId, currentUser.getUserId());
        return addressMapper.toResponse(address);
    }

    @Override
    public Boolean addressExists(AddressExists addressExists) {
        UUID addressId = UUID.fromString(addressExists.getAddressId());
        log.debug("Checking if address with id: {} exists for user with id: {}", addressId, addressExists.getUserId());
        return addressRepository.existsByUserIdAndId(UUID.fromString(addressExists.getUserId()), addressId);
    }

    @Override
    public void deleteAddress(String addressId) {
        Address address = addressRepository.findById(UUID.fromString(addressId))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        log.debug("Address found: {}", address);
        if (!address.getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException("You are not authorized to delete this address");
        } else {
            addressRepository.delete(address);
        }
    }

}
