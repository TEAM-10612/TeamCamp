package TeamCamp.demo.dto;

import TeamCamp.demo.domain.model.users.user.address.Address;
import lombok.*;


public class AddressBookDto {


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SaveRequest {

        private Long id;
        private String addressName;
        private String roadAddress;
        private String detailAddress;
        private String postalCode;

        @Builder
        public SaveRequest(Long id, String addressName, String roadAddress,
                           String detailAddress, String postalCode) {
            this.id = id;
            this.addressName = addressName;
            this.roadAddress = roadAddress;
            this.detailAddress = detailAddress;
            this.postalCode = postalCode;
        }


        public Address toEntity() {
            return Address.builder()
                    .addressName(this.addressName)
                    .detailAddress(this.detailAddress)
                    .roadAddress(this.roadAddress)
                    .postalCode(this.postalCode)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class IdRequest {
        private Long id;

        @Builder
        public IdRequest(Long id) {
            this.id = id;
        }
    }
}
