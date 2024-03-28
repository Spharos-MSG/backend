package spharos.msg.domain.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spharos.msg.domain.users.dto.request.AddressRequestDto;
import spharos.msg.domain.users.service.AddressService;
import spharos.msg.global.api.ApiResponse;
import spharos.msg.global.api.code.status.SuccessStatus;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/address")
@Tag(name = "배송지", description = "배송지 관련 API")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "배송지 추가", description = "해당 회원의 배송지를 추가 합니다.")
    @PutMapping("add")
    public ApiResponse<?> authenticateEmail(
            @RequestBody AddressRequestDto addressRequestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        addressService.createAddress(addressRequestDto, userDetails.getUsername());
        return ApiResponse.of(SuccessStatus.DELIVERY_ADDRESS_ADD_SUCCESS, null);
    }
}
