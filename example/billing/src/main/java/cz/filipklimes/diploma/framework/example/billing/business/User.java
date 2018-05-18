package cz.filipklimes.diploma.framework.example.billing.business;

import lombok.Getter;
import lombok.Setter;

public class User
{

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String role;

    public User(final String id, final String role)
    {
        this.id = id;
        this.role = role;
    }

    public User()
    {
    }

}
