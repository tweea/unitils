package org.unitils.parameterized;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.UnitilsParameterized;

/**
 * Test {@link UnitilsParameterized}
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * @since 3.4
 */
@RunWith(UnitilsParameterized.class)
public class UnitilsParametersNullParametersTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnitilsParametersNullParametersTest.class);

    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {
            {
                1
            }, {
                2
            }, {
                3
            }, {
                null
            }
        };
        return Arrays.asList(data);
    }

    private Integer number;

    public UnitilsParametersNullParametersTest(Integer number) {
        this.number = number;
    }

    @Test
    public void test() {
        LOGGER.debug("{}", number);
    }
}
