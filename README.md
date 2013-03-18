la-clojure
==========

Clojure plugin for IntelliJ IDEA.  
 Provides Clojure language support: syntax and error highlighting, completion, navigation and refactorings.

Getting Started
--
You have decided to develop in Clojure with your favorite IDE.  Great!  This README will assume that you have already downloaded and installed the "la-clojure" plugin via "Settings -> Plugins".

If you are familiar with Clojure, your first intuition will be to try to open a REPL in the IDE.  However, before you do that, you must first create a Clojure project.

This README assumes that you already have a JDK installed.  

1.  Click "File -> New Project -> Create Project from Scratch".
2.  Enter a name for your project, and select "Java Module" as the type.  Click "Next".
3.  Create a new source directory (or not) and click "Next".
4.  In the "Desired technologies" list check the "Clojure" checkbox.  You can choose to "Download" the Clojure jar (version 1.2) or select "Set up library later" to specify your own version of `clojure.jar`.
5.  Click "Finish" and the project will be created.

If you selected "Set up library later", configure the library as below:

1.  Download the latest version of the Clojure jar from [clojure.org](http://clojure.org/downloads)
2.  Right click on the project to "Open Module Settings"
3.  Click on "Modules -> Dependencies".
4.  Click "Add -> Single Entry Module Library", and select the path where you downloaded `clojure.jar` to.
5.  Click "Apply" and "OK".

Now you can start a Clojure Console via "Tools -> Clojure Console" or "Ctrl+Shift+F10".


Develop (Clojure) with Pleasure!
