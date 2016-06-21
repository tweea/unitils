package org.unitils.jbehave;

import org.unitils.jbehave.core.reporters.UnitilsFormats;


/**
 * explain how to use the testlink format.
 *
 * @author Willemijn Wouters
 *
 * @since
 *
 */
public class TestlinkTest extends UnitilsJUnitStories {


    //START SNIPPET: jbehavetestlinktest
    /**
     * @see org.unitils.jbehave.UnitilsJUnitStories#configureJBehave()
     */
    @Override
    public JBehaveConfiguration configureJBehave() {
        return super.configureJBehave()
            .addSteps(new SimpleMailStep())
            .addFormat(UnitilsFormats.TESTLINK);
    }
  //END SNIPPET: jbehavetestlinktest
}
