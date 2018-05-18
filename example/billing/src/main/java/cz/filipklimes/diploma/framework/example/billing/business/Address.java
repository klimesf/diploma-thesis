package cz.filipklimes.diploma.framework.example.billing.business;

import lombok.Getter;

public class Address
{

    @Getter
    private String country;

    @Getter
    private String city;

    @Getter
    private String street;

    @Getter
    private String postal;

    public Address()
    {
    }

    public Address(
        final String country,
        final String city,
        final String street,
        final String postal
    )
    {
        this.country = country;
        this.city = city;
        this.street = street;
        this.postal = postal;
    }

}
