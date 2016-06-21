package org.unitils.jbehave;

import org.unitils.jbehave.core.reporters.SeleniumScreenshotReporter;
import org.unitils.jbehave.core.reporters.SeleniumSteps;


/**
 * explain how to .
 *
 * @author wiw
 *
 * @since
 *
 */
public class ScreenshotTest extends UnitilsJUnitStories{

  //START SNIPPET: jbehavescreenshotsstep
    /**
     * @see org.unitils.jbehave.UnitilsJUnitStories#configureJBehave()
     */
    @Override
    public JBehaveConfiguration configureJBehave() {
        SeleniumSteps yourSeleniumSteps = new SendMailSeleniumSteps();
        return super.configureJBehave()
            .addSteps(yourSeleniumSteps)
            .addStoryReporter(new SeleniumScreenshotReporter("screenshots", yourSeleniumSteps))
            .addStepsWithSeleniumReporter(new SendMailSeleniumSteps(), "screenshots");
    }

  //END SNIPPET: jbehavescreenshotsstep
}
