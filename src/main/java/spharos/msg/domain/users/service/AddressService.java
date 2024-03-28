package spharos.msg.domain.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spharos.msg.domain.users.dto.request.AddressRequestDto;
import spharos.msg.domain.users.entity.Address;
import spharos.msg.domain.users.entity.Users;
import spharos.msg.domain.users.repository.AddressRepository;
import spharos.msg.domain.users.repository.UsersRepository;
import spharos.msg.global.api.code.status.ErrorStatus;
import spharos.msg.global.api.exception.UsersException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final UsersRepository usersRepository;

    public void createAddress(AddressRequestDto addressRequestDto, String uuid) {
        Users users = usersRepository.findByUuid(uuid).orElseThrow(
                () -> new UsersException(ErrorStatus.DELIVERY_ADDRESS_ADD_FAIL)
        );

        addressRepository.save(Address
                .builder()
                .mobileNumber(addressRequestDto.getMobileNumber())
                .addressName(addressRequestDto.getAddressName())
                .addressPhoneNumber(addressRequestDto.getAddressPhoneNumber())
                .recipient(addressRequestDto.getRecipient())
                .users(users)
                .address(addressRequestDto.getAddress())
                .build());
    }
}