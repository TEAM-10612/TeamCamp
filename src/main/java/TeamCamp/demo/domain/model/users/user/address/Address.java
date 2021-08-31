package TeamCamp.demo.domain.model.users.user.address;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import TeamCamp.demo.dto.AddressBookDto;


import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {
    private String addressName; //주소명
    private String roadAddress; //도로명주소
    private String detailAddress; //상세주소
    private String postalCode; //우편번호

    public void updateAddress(AddressBookDto request) {
        this.addressName = request.getAddressName();
        this.roadAddress = request.getRoadNameAddress();
        this.detailAddress = request.getDetailAddress();
        this.postalCode = request.getPostalCode();
    }
}
