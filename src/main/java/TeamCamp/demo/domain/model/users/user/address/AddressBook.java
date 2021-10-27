package TeamCamp.demo.domain.model.users.user.address;

import lombok.*;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
public class AddressBook {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL /*, orphanRemoval = true*/)
    @JoinColumn(name = "ADDRESSBOOK_ID")
    private List<Address> addressList = new ArrayList<>();

    public void addAddress(Address address) {
        addressList.add(address);
    }

    public void deleteAddress(Address address) {
        addressList.remove(address);
    }

    public Address findAddress(Long addressId) {
        return addressList.stream()
                .filter(address -> address.getId().equals(addressId))
                .findAny()
                .orElseThrow();
    }
}
