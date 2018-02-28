package cz.filipklimes.diploma.framework.example.order.model;

import lombok.Getter;
import lombok.Setter;

public class Address implements Entity
{

    @Getter
    @Setter
    private Long id;

    @Getter
    private String country;

    @Getter
    private String city;

    @Getter
    private String street;

    @Getter
    private String postalCode;

    @Getter
    private String houseNumber;

    public Address(
        final String country,
        final String city,
        final String street,
        final String postalCode,
        final String houseNumber
    )
    {
        this.country = country;
        this.city = city;
        this.street = street;
        this.postalCode = postalCode;
        this.houseNumber = houseNumber;
    }

}
