package TeamCamp.demo.domain.model.users.user.address;

import lombok.*;
import TeamCamp.demo.dto.AddressBookDto;


import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue
    private Long id;
    private String addressName; //주소명
    private String roadAddress; //도로명주소
    private String detailAddress; //상세주소
    private String postalCode; //우편번호

    public void updateAddress(AddressBookDto.SaveRequest request) {
        this.addressName = request.getAddressName();
        this.roadAddress = request.getRoadAddress();
        this.detailAddress = request.getDetailAddress();
        this.postalCode = request.getPostalCode();
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
