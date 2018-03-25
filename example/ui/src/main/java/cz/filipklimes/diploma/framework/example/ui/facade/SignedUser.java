package cz.filipklimes.diploma.framework.example.ui.facade;

import cz.filipklimes.diploma.framework.example.ui.business.User;
import org.springframework.stereotype.Service;

/**
 * Persist currently signed user, this is only for purposes of the prototype.
 * Please do not do this in a real-world application.
 */
@Service
public class SignedUser
{

    private User signedUser;

    public SignedUser()
    {
        signedUser = null;
    }

    public void signIn(final User user)
    {
        this.signedUser = user;
    }

    public User getCurrentlyLoggedUser()
    {
        return signedUser;
    }

    public boolean isAnyoneSignedIn()
    {
        return signedUser != null;
    }

}
