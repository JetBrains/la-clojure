package org.jetbrains.plugins.clojure;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import junit.framework.TestCase;

/**
 * @author ilyas
 */
public abstract class ClojureBaseTestCase extends TestCase {

  protected static final String TEST_FILE_EXT = ".test";

  private Project myProject;

  public Project getProject() {
    return myProject;
  }
  protected IdeaProjectTestFixture myFixture;

  protected void setUp() {
    myFixture = createFixture();

    try {
      myFixture.setUp();
    } catch (Exception e) {
      e.printStackTrace();
    } catch (AssertionError ae) {
      // mute
    }
    myProject = myFixture.getProject();
    ClojureLoader.loadClojure();
  }

  protected IdeaProjectTestFixture createFixture() {
    TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = IdeaTestFixtureFactory.getFixtureFactory().createLightFixtureBuilder();
    return fixtureBuilder.getFixture();
  }

  protected void tearDown() {
    ApplicationManager.getApplication().invokeLater(new Runnable() {
      public void run() {
        try {
          myFixture.tearDown();
        } catch (Exception e) {
          // mute
        }
      }
    });
  }
}
