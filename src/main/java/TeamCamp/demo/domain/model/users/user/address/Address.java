package TeamCamp.demo.domain.model.users.user.address;

import lombok.*;
import TeamCamp.demo.dto.AddressDto;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue
    private Long id;
    private String addressName;
    private String roadAddress;
    private String detailAddress;
    private String postalCode;

    public void updateAddress(AddressDto.SaveRequest requestDto) {
        this.addressName = requestDto.getAddressName();
        this.roadAddress = requestDto.getRoadAddress();
        this.detailAddress = requestDto.getDetailAddress();
        this.postalCode = requestDto.getPostalCode();
    }

    @Builder
    public Address(String addressName, String roadAddress, String detailAddress,
                   String postalCode) {
        this.addressName = addressName;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.postalCode = postalCode;
    }
}
