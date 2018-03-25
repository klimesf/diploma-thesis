package cz.filipklimes.diploma.framework.example.ui.business;

import lombok.Getter;

public class User
{

    @Getter
    private Integer id;

    @Getter
    private String name;

    @Getter
    private String email;

    @Getter
    private String role;

    public User()
    {
    }

    public User(final Integer id, final String name, final String email, final String role)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

}
