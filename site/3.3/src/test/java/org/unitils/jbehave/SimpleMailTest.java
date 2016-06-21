package org.unitils.jbehave;



/**
 * Test if everything from the {@link org.unitils.mail.MailModule} works.
 *
 * @author Willemijn Wouters
 *
 * @since 1.0.0
 *
 */
//START SNIPPET: jbehavetest
public class SimpleMailTest extends UnitilsJUnitStories {

    /**
     * @see org.unitils.jbehave.UnitilsJUnitStories#configureJBehave()
     */
    @Override
    public JBehaveConfiguration configureJBehave() {
        return super.configureJBehave()
            .addSteps(new SimpleMailStep())
            .storyFile("Mail.story")
            .storyPackage("org/unitils/jbehave/stories");
    }

}
//END SNIPPET: jbehavetest