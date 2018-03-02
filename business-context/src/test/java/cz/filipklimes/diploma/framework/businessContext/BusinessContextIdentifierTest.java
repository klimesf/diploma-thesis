package cz.filipklimes.diploma.framework.businessContext;

import org.junit.Assert;
import org.junit.Test;

public class BusinessContextIdentifierTest
{

    @Test
    public void testParseOk()
    {
        BusinessContextIdentifier identifier = BusinessContextIdentifier.parse("user.validEmail");

        Assert.assertEquals("user", identifier.getPrefix());
        Assert.assertEquals("validEmail", identifier.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseNoPrefix()
    {
        BusinessContextIdentifier.parse("validEmail");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseIllegalCharacters()
    {
        BusinessContextIdentifier.parse("u_s_e_r.validEmail?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseMultiplePrefixes()
    {
        BusinessContextIdentifier.parse("prefix.user.validEmail");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseBlankPrefix()
    {
        BusinessContextIdentifier.parse(".validEmail");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseBlankName()
    {
        BusinessContextIdentifier.parse("user.");
    }

}
