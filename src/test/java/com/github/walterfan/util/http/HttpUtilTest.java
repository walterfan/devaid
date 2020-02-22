package com.github.walterfan.util.http;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created by yafan on 9/8/2017.
 */
public class HttpUtilTest {

    @DataProvider
    public Object[][] makeHttpHeadFields() {

        return new Object[][] {
                { "acl_enabled= true", true },
                { "acl_enabled=true; auth_type=oauth", true },
                { "acl_enabled =TRue; auth_type=basic", true  },
                { "acl_enabled = false; auth_type=basic", false  },
                { " acl_enabled = ; auth_type=basic", false  },
                { "auth_type=basic", false  },
                { "", false  }

        };


    }

    @Test(dataProvider= "makeHttpHeadFields")
    public void testHasFieldValue(String toggleHeader, boolean ret) {
        Assert.assertEquals(HttpUtil.hasFieldValue(toggleHeader, "acl_enabled", "true") ,ret);

    }
}
