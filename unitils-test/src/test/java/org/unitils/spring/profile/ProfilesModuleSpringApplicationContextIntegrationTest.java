package org.unitils.spring.profile;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.spring.annotation.ConfigureProfile;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * ProfilesModuleSpringApplicationContextIntegrationTest.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * @since 3.4
 */
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
@SpringApplicationContext("classpath:org/unitils/spring/profile/applicationContext-dao-test.xml")
@ConfigureProfile(value = "dev", configuration = TypeConfiguration.APPLICATIONCONTEXT)
public class ProfilesModuleSpringApplicationContextIntegrationTest {
    @Autowired
    private DataSource dataSource;

    @Test
    public void test() {
        Assert.assertTrue(dataSource instanceof TransactionAwareDataSourceProxy);
    }
}
