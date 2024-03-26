package spharos.msg.global.api.code.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import spharos.msg.global.api.code.BaseErrorCode;
import spharos.msg.global.api.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러 입니다"),
    INVALID_PARAMETER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON501",
        "서버 에러, 유효하지 않은 매개변수입니다."),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "COMMON404", "잘못된 api 주소입니다."),
    INVALID_PATH_VARIABLE(HttpStatus.BAD_REQUEST, "COMMON405", "유효하지 않은 PathVariable 입니다."),
    INVALID_REQUEST_PARAM(HttpStatus.BAD_REQUEST, "COMMON406", "유효하지 않은 파라미터 요청입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    PRODUCT_ERROR(HttpStatus.BAD_REQUEST, "PRODUCT400", "홈 상품 조회 불러오기 실패"),

    SIGN_UP_UNION_FAIL(HttpStatus.BAD_REQUEST, "USER401", "통합 회원가입 실패"),
    SIGN_UP_EASY_FAIL(HttpStatus.BAD_REQUEST, "USER402", "간편 회원가입 실패"),
    LOGIN_ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER403", "통합 로그인 실패"),
    LOGIN_ID_PW_VALIDATION(HttpStatus.BAD_REQUEST, "USER404", "통합 로그인 실패"),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "USER405", "토큰이 만료되었습니다."),
    REISSUE_TOKEN_FAIL(HttpStatus.BAD_REQUEST, "USER406", "토큰 재발급 실패"),
    TOKEN_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "USER407", "잘못된 토큰 값 입니다"),
    EASY_LOGIN_ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER408", "간편 로그인 실패"),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "USER409", "이미 사용중인 Email 입니다"),
    ALREADY_EASY_USER(HttpStatus.BAD_REQUEST, "USER410", "이미 간편 회원 입니다."),
    IS_NOT_EASY_USER(HttpStatus.BAD_REQUEST, "USER411", "간편 회원이 아닙니다."),

    INVALID_ORDER(HttpStatus.BAD_REQUEST, "ORDER400", "잘못된 주문 요청입니다"),
    ORDER_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER401", "주문 상품을 찾을 수 없습니다"),
    ORDER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER404", "주문 정보를 찾을 수 없습니다."),
    ORDER_ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER405", "주소 정보를 찾을 수 없습니다"),
    NOT_EXIST_PRODUCT_OPTION(HttpStatus.NOT_FOUND, "PRODUCT600", "존재하지 않는 상품옵션 입니다."),
    NOT_EXIST_PRODUCT(HttpStatus.NOT_FOUND, "PRODUCT601", "존재하지 않는 상품입니다."),


    ALREADY_HAD_COUPON(HttpStatus.FORBIDDEN,"COUPON701","이미 보유중인 쿠폰입니다."),
    ALREADY_USED_COUPON(HttpStatus.FORBIDDEN,"COUPON701","이미 사용한 쿠폰입니다."),
    NOT_CART_OWNER(HttpStatus.FORBIDDEN,"CART801","장바구니 주인이 아닙니다."),

    REVIEW_SAVE_FAIL(HttpStatus.BAD_REQUEST, "REVIEW400","상품 리뷰 작성 실패"),
    REVIEW_UPDATE_FAIL(HttpStatus.BAD_REQUEST, "REVIEW400","상품 리뷰 수정 실패"),
    REVIEW_DELETE_FAIL(HttpStatus.BAD_REQUEST,"REVIEW400","상품 리뷰 삭제 실패"),
    REVIEW_READ_FAIL(HttpStatus.BAD_REQUEST, "REVIEW400","상품 리뷰 조회 실패"),
    EXAMPLE_EXCEPTION(HttpStatus.BAD_REQUEST, "EXAMPLE400", "샘플 에러 메시지입니다");


    private final HttpStatus httpStatus;
    private final String status;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
            .message(message)
            .status(status)
            .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
            .message(message)
            .status(status)
            .httpStatus(httpStatus)
            .build();
    }
}
