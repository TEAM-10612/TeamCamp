package TeamCamp.demo.util;

import TeamCamp.demo.exception.product.ImageRoadFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseConstants {
    public static final ResponseEntity OK = ResponseEntity.ok().build();

    public static final ResponseEntity<Void> CREATED =
            ResponseEntity.status(HttpStatus.CREATED).build();


    public static final ResponseEntity BAD_REQUEST = ResponseEntity.badRequest().build();

    public static final ResponseEntity<String> DUPLICATION_NICKNAME = new ResponseEntity<>(
            "닉네임 중복", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> DUPLICATION_EMAIL = new ResponseEntity<>(
            "이메일 중복", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> USER_NOT_FOUND =
            new ResponseEntity<>(
                    "ID 또는 PW를 확인하세요.", HttpStatus.NOT_FOUND
            );

    public static final ResponseEntity<String> UNAUTHORIZED_USER =
            new ResponseEntity<>(
                    "Unauthenticated user", HttpStatus.UNAUTHORIZED
            );

    public static final ResponseEntity FAIL_TO_CHANGE_NICKNAME =
            new ResponseEntity("닉네임은 7일에 한번씩 변경이 가능합니다.",HttpStatus.BAD_REQUEST);

    public static final ResponseEntity<String>WRONG_PASSWORD =
            new ResponseEntity<>("잘못된 비밀번호입니다. 비밀번호를 확인해주세요.", HttpStatus.UNAUTHORIZED);

    public static final ResponseEntity<String> PRODUCT_NOT_FOUND =
            new ResponseEntity<>("존재하지 않는 상품입니다.",HttpStatus.BAD_REQUEST);

    public static final ResponseEntity<String> TOKEN_EXPIRED =
            new ResponseEntity<>("인증 토큰이 만료되었습니다. 마이페이지에서 인증 토큰 재전송 버튼을 클릭해주세요.",HttpStatus.UNAUTHORIZED);

    public static final ResponseEntity<String>NOT_AUTHORIZED =
            new ResponseEntity<>("해당 리소스에 대한 접근 권한이 없습니다.",HttpStatus.FORBIDDEN);

    public static final ResponseEntity<String> ILLEGAL_MIME_TYPE =
            new ResponseEntity<>("첨부파일의 확장자가 올바르지 않습니다.",HttpStatus.BAD_REQUEST);

    public static final ResponseEntity<String> IMAGE_ROAD_FAILED =
            new ResponseEntity<>("이미지 로드에 실패하였습니다.",
                    HttpStatus.BAD_REQUEST);

    public static final ResponseEntity<String> DUPLICATION_WISHLIST_PRODUCT = new ResponseEntity<>(
            "해당 제품은 이미 위시리스트에 등록되어 있는 상품 입니다.",
            HttpStatus.CONFLICT
    );

    public static final ResponseEntity<String> IMAGE_TO_LARGE =
            new ResponseEntity<>("허용된 용량을 초과한 이미지 입니다.",
                    HttpStatus.PAYLOAD_TOO_LARGE);
}


