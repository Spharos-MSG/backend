package spharos.msg.domain.users.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "address_name")
    private String addressName;

    @NotBlank
    @Column(name = "recipient")
    private String recipient;

    @NotBlank
    @Column(name = "mobile_number")
    private String mobileNumber;

    @NotBlank
    @Column(name = "address_phone_number")
    private String addressPhoneNumber;

    @NotBlank
    @Column(name = "address")
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @Builder
    public Address(String addressName, String recipient, String mobileNumber,
            String addressPhoneNumber,
            String address, Users users) {
        this.addressName = addressName;
        this.recipient = recipient;
        this.mobileNumber = mobileNumber;
        this.addressPhoneNumber = addressPhoneNumber;
        this.address = address;
        this.users = users;
    }
}
