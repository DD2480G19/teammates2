package teammates.ui.webapi;

import java.time.Instant;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.request.Intent;

/**
 * SUT: {@link SubmitFeedbackResponsesAction}.
 */
public class SubmitFeedbackResponsesActionTest extends BaseActionTest<SubmitFeedbackResponsesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    public void testExecute() {
        // See each independent test case.
    }

    @Override
    protected void testAccessControl() {
        // See each independent test case.
    }

    @Test
    public void testExecute_feedbackQuestionDoesNotExist_shouldThrowEntityNotFoundException() throws Exception {
        String questionNumber = "999";
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionNumber,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString()
        };

        ______TS("Question does not exist; should throw exception.");

        verifyEntityNotFound(submissionParams);
    }

    @Test
    public void testExecute_intentIsUnknown_shouldThrowInvalidHttpParameterException() throws Exception {
        int questionNumber = 1;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString()
        };

        ______TS("Intent is unknown; should throw exception.");

        verifyHttpParameterFailure(submissionParams);
    }

    @Test
    public void testAccessControl_instructorSubmissionPastEndTime_shouldAllowIfBeforeDeadline() throws Exception {
        int questionNumber = 4;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackQuestionAttributes qn4InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);

        Instant newEndTime = TimeHelper.getInstantDaysOffsetFromNow(-2);
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withInstructorDeadlines(Map.of())
                .withEndTime(newEndTime)
                .build());
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn4InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        ______TS("No selective deadline; should fail.");

        verifyCannotAccess(submissionParams);

        ______TS("After selective deadline; should fail.");

        Map<String, Instant> newInstructorDeadlines = Map.of(
                instructor1OfCourse1.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(-1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withInstructorDeadlines(newInstructorDeadlines)
                .build());
        verifyCannotAccess(submissionParams);

        ______TS("Before selective deadline; should pass.");

        newInstructorDeadlines = Map.of(
                instructor1OfCourse1.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withInstructorDeadlines(newInstructorDeadlines)
                .build());
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionPastEndTime_shouldAllowIfBeforeDeadline() throws Exception {
        int questionNumber = 1;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);

        Instant newEndTime = TimeHelper.getInstantDaysOffsetFromNow(-2);
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withEndTime(newEndTime)
                .build());
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("No selective deadline; should fail.");

        verifyCannotAccess(submissionParams);

        ______TS("After selective deadline; should fail.");

        Map<String, Instant> newStudentDeadlines = Map.of(
                student1InCourse1.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(-1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withStudentDeadlines(newStudentDeadlines)
                .build());
        verifyCannotAccess(submissionParams);

        ______TS("Before selective deadline; should pass.");

        newStudentDeadlines = Map.of(
                student1InCourse1.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                .withStudentDeadlines(newStudentDeadlines)
                .build());
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentDoesNotExist_shouldThrowEntityNotFoundException() throws Exception{
        int questionNumber = 1;
        FeedbackSessionAttributes sessionInTestingWithoutStudent = typicalBundle.feedbackSessions.get("sessionInTestingWithoutStudent");
        String feedbackSessionName = sessionInTestingWithoutStudent.getFeedbackSessionName();
        String courseId = sessionInTestingWithoutStudent.getCourseId();
        FeedbackQuestionAttributes qn1InSessionInTestingWithoutStudent = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSessionInTestingWithoutStudent.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString()
        };

        ______TS("Student does not exist; should throw exception.");

        verifyEntityNotFoundAcl(submissionParams);
    }

    @Test
    public void testAccessControl_instructorDoesNotExist_shouldThrowEntityNotFoundException() throws Exception {
        int questionNumber = 1;
        FeedbackSessionAttributes sessionInTestingWithoutInstructor = typicalBundle.feedbackSessions
                .get("sessionInTestingWithoutInstructor");
        String feedbackSessionName = sessionInTestingWithoutInstructor.getFeedbackSessionName();
        String courseId = sessionInTestingWithoutInstructor.getCourseId();
        FeedbackQuestionAttributes qn1InSessionInTestingWithoutInstructor = logic.getFeedbackQuestion(
                feedbackSessionName,
                courseId, questionNumber);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSessionInTestingWithoutInstructor.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString()
        };

        ______TS("Instructor does not exist; should throw exception.");

        verifyEntityNotFoundAcl(submissionParams);
    }

    @Test
    public void testAccessControl_unknownIntent_shouldThrowInvalidHttpParameterException() throws Exception {
        int questionNumber = 4;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        FeedbackQuestionAttributes qn4InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn4InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString()
        };

        ______TS("Incorrect intent parameter; should throw exception.");

        verifyHttpParameterFailureAcl(submissionParams);
    }

    @Test
    public void testAccessControl_studentResultIntent_shouldThrowInvalidHttpParameterException() throws Exception {
        int questionNumber = 4;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        FeedbackQuestionAttributes qn4InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);
               
        String[] submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn4InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString()
        };
        
        ______TS("Student Result Intent; should throw exception.");
        
        verifyHttpParameterFailureAcl(submissionParams);
    }

    @Test
    public void testAccessControl_instructorResultIntent_shouldThrowInvalidHttpParameterException() throws Exception {
        int questionNumber = 4;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        FeedbackQuestionAttributes qn4InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn4InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString()
        };

        ______TS("Incorrect intent parameter; should throw exception.");

        verifyHttpParameterFailureAcl(submissionParams);
    }
}
