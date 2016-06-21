package org.unitils.jbehave;



/**
 * explain how to add the database steps for in the story file.
 *
 * @author Willemijn Wouters
 *
 * @since
 *
 */
public class SimpleMail2Test extends UnitilsJUnitStories {

    //START SNIPPET: jbehavedatabasestep
    /**
     * @see org.unitils.jbehave.UnitilsJUnitStories#configureJBehave()
     */
    @Override
    public JBehaveConfiguration configureJBehave() {
        return super.configureJBehave()
            .useDatabaseSteps()
            .addSteps(new SimpleMailStep());
    }
    //END SNIPPET: jbehavedatabasestep
}
