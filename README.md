<p align="center">
    <a href="https://github.com/DD2480G19/teammates2/commits/master">
        <img alt="Last Commit" src="https://img.shields.io/github/last-commit/DD2480G19/teammates2.svg?style=flat-square&logo=github&logoColor=white">
    </a>
    <a href="https://github.com/DD2480G19/teammates2/issues">
        <img alt="Issues" src="https://img.shields.io/github/issues-raw/DD2480G19/teammates2.svg?style=flat-square&logo=github&logoColor=white">
    </a>
    <a href="https://github.com/DD2480G19/teammates2/pulls">
        <img alt="Pull Requests" src="https://img.shields.io/github/issues-pr-raw/DD2480G19/teammates2.svg?style=flat-square&logo=github&logoColor=white">
    </a>
</p>

# Report for *Assignment #4: Issue resolution*

<img src="https://upload.wikimedia.org/wikipedia/en/thumb/e/e0/KTH_Royal_Institute_of_Technology_logo.svg/1200px-KTH_Royal_Institute_of_Technology_logo.svg.png" alt="KTH Logo" align="left" width="90" height="90" style="vertical-align:middle;margin:0px 15px">

This is group 19's repository for *Assignment #4: Issue resolution* in the course DD2480 Software Engineering Fundamentals at KTH Royal Institute of Technology, Sweden. The assignment description states: "The goal of this project to realize the complexity of issue resolution (or refactoring, but henceforth we will use “issue”) in a real project. What is being graded is the process through which you carry out your work, the quality of your work, and your reflections on it. Note that even a partially resolved issue can give you maximal points, as long as it is well managed and documented.".

## Project
The chosen project on which to perform the assignment is [TEAMMATES](https://github.com/TEAMMATES/teammates). TEAMMATES is a free online tool for managing peer evaluations and other feedback paths of your students. It is provided as a cloud-based service for educators/students and is currently used by hundreds of universities across the world.

<img src="src/web/assets/images/overview.png" width="600">

This is the developer web site for TEAMMATES. **Click [here](http://teammatesv4.appspot.com/) to go to the TEAMMATES product website.**

## Onboarding experience
We chose to continue with the project from the previous assignment. This is the text about the onboarding from the previous report:

*"The onboarding was pretty straight forward. The project's README included an easily accessible URL to "Setting Up" instructions, and elaborate documentation on an external website. The repository also contained all documentation in a directory: `docs`. The project's dependencies required some of us to downgrade our Java version, however, the instructions to configure the project accordingly were clearly described in the documentation.*

*The building process was run by first executing `./gradlew createConfigs`, then `./gradlew build`, which automatically installed the necessary components, without errors. If one wanted to install dependencies for front-end development, the steps for that were also clearly described in the documentation.*

*Back-end tests were run by executing `./gradlew componentTests`. Thanks to gradle all component tests were run automatically, and when 635 tests had been run the testing was manually stopped. Of all tests, only 7 failed. Examples are failures that occurred due to lack of third-party dependencies (email services), and/or date/time tests that failed because they were run on a Swedish OS.*

*In conclusion, the onboarding experience was smooth and we plan to continue with the project."*

## Effort spent
> ***P criterion 2: The contribution of each group member is documented. The work carried out is commensurate with the expected time spent (20–25 hours per person).***

> For each team member, how much time was spent in
> 1. plenary discussions/meetings;
> 2. discussions within parts of the group;
> 3. reading documentation;
> 4. analyzing code/output;
> 5. writing documentation;
> 6. writing code;
> 7. running code?
> 
> For setting up tools and libraries (step 4), enumerate all dependencies
you took care of and where you spent your time, if that time exceeds
30 minutes.

> **From assignment description:**
> Document your experience. Keep track of how your team spends its time.
> 
> Note: We might have to change format from a table if it doesn't look good when filling it in.

Time spent for each team member and category [hours]:
| Team member | 1 | 2 | 3 | 4 | 5 | 6 | 7 | In total |
|-------------|---|---|---|---|---|---|---|----------|
| <a href="https://github.com/edbag22"><img src="https://avatars.githubusercontent.com/u/78201117?v=4" width="25" height="25" style="vertical-align:middle;margin:0px 5px" alt="Edvin" align="left"/></a> Edvin  | | | | | | | | |
| <a href="https://github.com/gustafssonlinnea"><img src="https://avatars.githubusercontent.com/u/70338667?v=4" width="25" height="25" style="vertical-align:middle;margin:0px 5px" alt="Edvin" align="left"/></a> Linnéa | 4.5 | 2 | 1 | 6 | 9.25 | | | 22.75 |
| <a href="https://github.com/markusnewtonh"><img src="https://avatars.githubusercontent.com/u/61276335?v=4" width="25" height="25" style="vertical-align:middle;margin:0px 5px" alt="Edvin" align="left"/></a>  Markus |3.9|1.6|1.5|6.0|1.5|7.7|1|21.7|
| <a href="https://github.com/ElHachem02"><img src="https://avatars.githubusercontent.com/u/100425207?v=4" width="25" height="25" style="vertical-align:middle;margin:0px 5px" alt="Edvin" align="left"/></a>  Peter  | | | | | | | | |
| <a href="https://github.com/FalkWasTakena"><img src="https://avatars.githubusercontent.com/u/71826609?v=4" width="25" height="25" style="vertical-align:middle;margin:0px 5px" alt="Edvin" align="left"/></a>  Samuel | | | | | | | | |

## Overview of issue(s) and work done
The issue we worked with is [***Add unit tests for `SubmitFeedbackResponsesAction` (#11826)***](https://github.com/TEAMMATES/teammates/issues/11826). The description of it is "`SubmitFeedbackResponsesAction` is by far the most important and the most used state-changing API of the system, and yet there are no unit tests for it". Functionality affected is (obviously) the testing of the class `SubmitFeedbackResponsesAction`. Code affected directly is `SubmitFeedbackResponsesActionTest`, and indirectly`SubmitFeedbackResponsesAction`. `typicalDataBundle` is also affected directly, which in turn affects a significant part of the other tests since we had to make sure they still work the same way after our modifications.


## Requirements for the new feature or requirements affected by functionality being refactored
> ***P criterion 4: Requirements related to the functionality are identified and described in a systematic way. Each requirement has a name (ID), title, and description. The description can be one paragraph per requirement.***

> ***P+ criterion 3: Relevant test cases (existing tests and updated/new tests related to the refactored code) are traced to requirements.***

> Optional (point 3): trace tests to requirements.

> **From assignment description:**
> Identify requirements related to the issue. If the requirements are not documented yet, try to describe them based on code reviews and existing test cases. Create a project plan for testing these requirements, and working on the issue.

In this section, we've tried to identify all untested requirements for the two methods related to the issue. The tables can be seen in the two  subsections below: 

### Identified requirements for `checkSpecificAccessControl`
| ID    | Title | Description | Issue |
| ----- | ----- | ----------- | ----- |
| cSAC1 | `checkSpecificAccessControl`: Exception when feedback question does not exist | The action should throw `EntityNotFoundException("The feedback question does not exist.")` when feedback question does not exist (i.e., `feedbackQuestion == null`). | [#21](https://github.com/DD2480G19/teammates2/issues/21) |
| cSAC2 | `checkSpecificAccessControl`: Exception when student does not exist | The action should throw `EntityNotFoundException("Student does not exist.")` when student does not exist (i.e.,  `studentAttributes == null`). | [#22](https://github.com/DD2480G19/teammates2/issues/22) |
| cSAC3 | `checkSpecificAccessControl`: Exception when instructor does not exist | The action should throw `EntityNotFoundException("Instructor does not exist.")` when instructor does not exist (i.e.,  `instructorAttributes == null`). | [#23](https://github.com/DD2480G19/teammates2/issues/23) |
| cSAC4 | `checkSpecificAccessControl`: Exception when student result intent | The action should throw `InvalidHttpParameterException("Invalid intent for this action")` when intent is student result (i.e.,  `intent == STUDENT_RESULT`). | [#24](https://github.com/DD2480G19/teammates2/issues/24) |
| cSAC5 | `checkSpecificAccessControl`: Exception when intent is unknown | The action should throw `InvalidHttpParameterException("Unknown intent " + intent)` when intent is unknown (i.e.,  `intent != STUDENT_SUBMISSION`, `INSTRUCTOR_SUBMISSION`, `INSTRUCTOR_RESULT`, or `STUDENT_RESULT`). | [#25](https://github.com/DD2480G19/teammates2/issues/25) |
| cSAC6 | `checkSpecificAccessControl`: Exception when student result intent | The action should throw `InvalidHttpParameterException("Invalid intent for this action")` when intent is instructor result (i.e.,  `intent == INSTRUCTOR_RESULT`). | [#49](https://github.com/DD2480G19/teammates2/issues/49) |

### Identified requirements for `execute`
| ID   | Title | Description | Issue |
| ---- | ----- | ----------- | ----- |
| ex01 | `execute`: Exception when feedback question does not exist | The action should throw `EntityNotFoundException("The feedback question does not exist.")` when feedback question does not exist (i.e., `feedbackQuestion == null`). | [#29](https://github.com/DD2480G19/teammates2/issues/29) |
| ex02 | `execute`: If student submission, result is valid | If a student submits, the action should produce a valid JSON result (and not throw any exceptions). | [#34](https://github.com/DD2480G19/teammates2/issues/34) |
| ex03 | `execute`: If student giver and not type `TEAMS`, `giverIndentifier` is set to student email | If giver is a student and the giver type is not `TEAMS`, `giverIdentifier` should be set to the student’s email. | [#36](https://github.com/DD2480G19/teammates2/issues/36) |
| ex04 | `execute`: If student giver and type `TEAMS`, `giverIndentifier` is set to team name | If giver is a student and the giver type is `TEAMS`, `giverIdentifier` should be set to the team name. | [#36](https://github.com/DD2480G19/teammates2/issues/36) |
| ex05 | `execute`: Exception when intent is unknown | The action should throw `InvalidHttpParameterException("Unknown intent " + intent)` when intent is unknown (i.e.,  `intent != STUDENT_SUBMISSION` or `INSTRUCTOR_SUBMISSION`). | [#30](https://github.com/DD2480G19/teammates2/issues/30) |
| ex06 | `execute`: Exception when recipient is not valid for the question | The action should throw `InvalidOperationException("The recipient " + recipient + " is not a valid recipient of the question")` recipient is not valid for the question (i.e., `!recipientsOfTheQuestion.containsKey(recipient)`). | [#31](https://github.com/DD2480G19/teammates2/issues/31) |
| ex07 | `execute`: If instructor submission, result is valid | If an instructor submits, the action should produce a valid JSON result (and not throw any exceptions). | [#35](https://github.com/DD2480G19/teammates2/issues/35) |
| ex08 | `execute`: If existing responses for recipient, responses are updated | If there are existing responses for a recipient, the responses should be updated and reflected in the JSON result/database. | [#37](https://github.com/DD2480G19/teammates2/issues/37) |
| ex09 | `execute`: If no existing responses for recipient, new responses are added | If there are no existing responses for a recipient, the new responses should just be added and reflected in the JSON result/database. | [#42](https://github.com/DD2480G19/teammates2/issues/42) |
| ex10 | `execute`: If no responses in submission request, clear database | If there are no responses in the submission request (`submitRequest.getResponses()`), all previous responses from the giver for that question should be cleared.  <span style="color:red">*(Note: This is not reflected in the Json result!)*</span> | [#45](https://github.com/DD2480G19/teammates2/issues/45) |
| ex11 | `execute`: If feedback responses are not valid, exception is thrown | If a feedback response is not valid (e.g., not compliant with the question format), an `InvalidHttpRequestBodyException` is thrown with a message related to the specific question. | [#39](https://github.com/DD2480G19/teammates2/issues/39) |
| ex12 | `execute`: Number of recipients are the max possible | If the number of recipients specified by the question is equal to the max number of recipients (`numRecipients == Const.MAX_POSSIBLE_RECIPIENTS`), the number of recipients is set to the value specified by the submission (`recipientsOfTheQuestion.size()`). <span style="color:red">*(Note: This seems contrived and the purpose of the code is not clear. With this in mind, we skip this for now.)*</span> |  |
| ex13 | `execute`: Number of recipients are greater than number of recipients for the submission | If the number of recipients specified by the question is larger the the number of recipients specified by the submission (`numRecipients > recipientsOfTheQuestion.size()`), the number of recipients is set to the value specified by the submission (`recipientsOfTheQuestion.size()`). <span style="color:red">*(Note: This seems contrived and the purpose of the code is not clear. With this in mind, we skip this for now.)*</span> |  |

## Code changes
> ***P criterion 5: Changes to the code and test suite are shown and documented, e.g., as a patch.***

### Patch
> ***P+ criterion 4: Your patch is clean in that it (a) removes but does not comment out obsolete code and (b) does not produce extraneous output that is not required for your task (such as debug output) and (c) does not add unnecessary whitespace changes (such as adding or removing empty lines).***

> ***P+ criterion 5: Patches are accepted by the project, or considered for acceptance. (This requires a link to an accepted commit, or a discussion item.) Note: the patch must be submitted by the assignment deadline, but it may be accepted later. (Please notify us if this extra point is necessary for a P+.)***

> (copy your changes or the add git command to show them)
> `git diff ...`
> Optional (point 4): the patch is clean.
> Optional (point 5): considered for acceptance (passes all automated checks).

## Test results
> ***P criterion 6: Tests are automated, their outcome is documented, e.g., as a test log.***

> Overall results with link to a copy or excerpt of the logs (before/after refactoring).

> **From assignment description:**
> Run the existing regression tests; make sure they succeed. If any tests fail, check if they may interfere
with your task. ***Keep copies of the test logs.*** If you there is a problem with the existing tests that
interferes with your task, document that as a new issue (in the issue tracker), and either try to resolve
it, or change your task to a different issue, perhaps in a different project.

## UML class diagram and its description
> ***P criterion 7: Key features affected by the issue are shown in UML class diagrams (for refactorings: include before/after). Note: you do not have to show classes, fields, or methods that are not relevant, unless they help with the overall understanding. Typically, the diagram would contain 5–10 classes.*** 

### Key changes/classes affected
> ***P+ criterion 1: The architecture and purpose of the system are presented in an overview of about 1–1.5 pages; consider using a diagram. Note: If you manage to improve on existing documentation or fill a gap in the project here, please consider making your documentation available to the project; they may be grateful for it!***

> ***P+ criterion 2: Updates in the source are put into context with the overall software architecture and discussed, relating them to design patterns and/or refactoring patterns.***

> Optional (point 1): Architectural overview.
> Optional (point 2): relation to design pattern(s).

### Architecture and purpose of the system
An overview of the architecture and purpose of the system can be find [here](https://drive.google.com/file/d/1PSz7TaH7jJx8tZEWGXm874XaHgWZf7ed/view?usp=sharing)

## Overall experience
> ***P criterion 8: The overall work carried out, and experience gained, are documented. In particular, mention your experience about the given documentation/examples of the project, its tool framework, and the interaction within your team (using the Essence framework) and with the community of the project.***

> ***P+ criterion 7: You have done something extraordinary that exceeds the scope of the assignment, and which you can be proud of.***

> What are your main take-aways from this project? What did you learn?
> How did you grow as a team, using the Essence standard to evaluate yourself?
> Optional (point 6): How would you put your work in context with best software engineering practice?
> Optional (point 7): Is there something special you want to mention here?

> **From assignment description:**
> If you were not able to finish all tasks, how much progress have you made? How much time do you think you would need to complete the task? 

### Main take-aways
From this projects, there are many take-aways. One is that as projects increase in size, well-structured development becomes increasingly important. Strict guidelines for the format of contributions are essential to keep code uniform and readable. TEAMMATES has a good and clear structure for this, for example seen in some parts in their documentation of the project, in the pull request workflow, etc. Although, in other ways, their documentation is insufficient. Due to this, it has sometimes been unnecessarily time-consuming getting to know the program. So, this is another take-away – that documentation is extra important for large projects with complex systems. 

### Essence evaluation
> ***P+ criterion 6: You can argue critically about the benefits, drawbacks, and limitations of your work carried out, in the context of current software engineering practice, such as the SEMAT kernel (covering alphas other than Team/Way of Working).***

> **From assignment description:**
> Assess your team (p. 51 in the [Essence standard](https://www.omg.org/spec/Essence/1.2/PDF) v1.2) by evaluating the checklist on p. 52:
> In what state are you in? Why? What are obstacles to reach the next state? How have you improved during the course, and where is more improvement possible?

The team is in the *Adjourned* state. Through the work on this assignment, we have had a clear mission: To improve the branch coverage relating to the chosen issue, and fulfill all requirements for both P and P+. We already had knowledge of the project from the previous assignment. We have had a very well-working team where everyone has contributed and communicated about progress and obstacles. The members' strengths have been of great benefit for the rest of the team. All work was kept track of with GitHub issues and a project board. We are now done with the final assignment, and no more tasks are to be done. There is no next state. 

During the course, we have improved our knowledge of larger software projects, and software engineering in general – not only relating directly to coding, but also planning, task-management, dividing responsibility, testing, documentation, and more. Although, there is obviously room for improvement. For example, we would have liked to work even more systematically to be able to avoid code duplication, inefficiency, etc. E.g., we could have given the team members different roles, had regular smaller meetings for those with similar tasks, and more.

In addition to the Team/Way of Working, the group’s work affects other alphas of the SEMAT essence:

### Solution

<a href="https://imgbb.com/"><img src="https://i.ibb.co/Cz9RtPX/req.png" alt="req" border="0"></a>

The identification of requirements related to the issue that the group has worked on contributes to the work on the Requirements alpha. The benefit of this is that the purpose and the expected outcome of the work are made clear for the parties involved, which also helps to outline how the work should be done. However, in order to progress on the checklist for Requirements, all stakeholders should accept the identified requirement. That state is not reached since the requirements have not been reviewed by the project’s founders.

<a href="https://imgbb.com/"><img src="https://i.ibb.co/sVPMh4Y/soft.png" alt="soft" border="0"></a>

Regarding the Software System alpha, the group’s work covers the part where an architecture has been selected and testing has been implemented. The implemented tests meet the requirements and are demonstrable, which constitutes important progress. In order for the implementation to reach the last states of the alpha, the tests need to be reviewed by the project lead and integrated into the live version of the project's code base.

### Endeavor

<a href="https://imgbb.com/"><img src="https://i.ibb.co/3pkhcNr/work.png" alt="work" border="0"></a>

This alpha, the Work alpha, is almost completely covered by the group’s work. The checklist’s different states can be easily tracked to related issues in the group’s project (Kanban) board on GitHub. Thanks to this, and great communication, the work throughout the assignment never halted. The limitations related to this alpha are foremost the state that requires the stakeholder(s) to accept the resulting software system, which is not yet reached. 

## P+ points aimed for
- *1. The architecture and purpose of the system are presented in an overview of about 1–1.5 pages; consider
using a diagram. Note: If you manage to improve on existing documentation or fill a gap in the project
here, please consider making your documentation available to the project; they may be grateful for it!*
- *3. Relevant test cases (existing tests and updated/new tests related to the refactored code) are traced to
requirements.*
- *4. Your patch is clean in that it (a) removes but does not comment out obsolete code and (b) does not
produce extraneous output that is not required for your task (such as debug output) and (c) does not add unnecessary whitespace changes (such as adding or removing empty lines).*
- *6. You can argue critically about the benefits, drawbacks, and limitations of your work carried out, in
the context of current software engineering practice, such as the [SEMAT kernel](http://semat.org/quick-reference-guide) (covering alphas other
than Team/Way of Working).*

