package teammates.ui.webapi;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.FeedbackResponsesRequest;
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
    public void testExecute_existingResponses_shouldBeUpdated() throws Exception {
        TestData data = dataWithStudent(1, "session1InCourse1", "student2InCourse1");

        loginAsStudent(data.student.getGoogleId());

        String[] submissionParams = getParams(data.question, Intent.STUDENT_SUBMISSION);
        String existingAnswer = typicalBundle.feedbackResponses
                .get("response2ForQ1S1C1").getResponseDetails().getAnswerString();

        var existingResponses = logic.getFeedbackResponsesFromStudentOrTeamForQuestion(data.question, data.student);
        FeedbackResponseAttributes existingResponse = existingResponses.get(0);

        ______TS("The existing response answer should be \"I'm cool'\"");
        assertEquals(existingAnswer, existingResponse.getResponseDetails().getAnswerString());

        // Modify the existing response
        existingResponse.setResponseDetails(new FeedbackTextResponseDetails("I'm not cool"));

        // Create a responses requestion with all existing responses
        FeedbackResponsesRequest responsesRequest = new FeedbackResponsesRequest();
        responsesRequest.setResponses(existingResponses.stream().map(r ->
                new FeedbackResponsesRequest.FeedbackResponseRequest(
                        existingResponse.getRecipient(),
                        existingResponse.getResponseDetails()
        )).collect(Collectors.toList()));

        SubmitFeedbackResponsesAction a = getAction(responsesRequest, submissionParams);
        JsonResult result = getJsonResult(a);
        FeedbackResponsesData responses = (FeedbackResponsesData) result.getOutput();
        FeedbackResponseData actualResponse = responses.getResponses().get(0);

        ______TS("Existing response has been modified; should be reflected in the result.");
        assertEquals(existingResponses.size(), responses.getResponses().size());
        verifyFeedbackResponseEquals(existingResponse, actualResponse);
    }

    @Test
    public void testExecute_studentSubmission_shouldProduceValidResult() throws Exception {
        TestData data = dataWithStudent(1, "session1InCourse1", "student1InCourse1");

        loginAsStudent(data.student.getGoogleId());
        String[] submissionParams = getParams(data.question, Intent.STUDENT_SUBMISSION);

        FeedbackTextResponseDetails responseDetails = new FeedbackTextResponseDetails("Updated response details");
        var responsesRequest = createSingletonResponsesRequest(data.student.getEmail(), responseDetails);

        SubmitFeedbackResponsesAction a = getAction(responsesRequest, submissionParams);
        JsonResult result = getJsonResult(a);
        FeedbackResponsesData responses = (FeedbackResponsesData) result.getOutput();
        FeedbackResponseData actualResponse = responses.getResponses().get(0);

        FeedbackResponseAttributes expectedResponse = logic.getFeedbackResponse(data.question.getId(),
                data.student.getEmail(), data.student.getEmail());
        expectedResponse.setResponseDetails(responseDetails);

        ______TS("Successful student submission; should produce valid result.");

        verifyFeedbackResponseEquals(expectedResponse, actualResponse);
        logoutUser();
    }

    @Test
    public void testExecute_instructorSubmission_shouldProduceValidResult() throws Exception {
        TestData data = dataWithInstructor(1, "session1InCourse1", "instructor1OfCourse1");

        loginAsInstructor(data.instructor.getGoogleId());
        String[] submissionParams = getParams(data.question, Intent.INSTRUCTOR_SUBMISSION);

        FeedbackTextResponseDetails responseDetails = new FeedbackTextResponseDetails("Updated response details");
        var responsesRequest = createSingletonResponsesRequest(data.instructor.getEmail(), responseDetails);

        SubmitFeedbackResponsesAction a = getAction(responsesRequest, submissionParams);
        JsonResult result = getJsonResult(a);
        FeedbackResponsesData responses = (FeedbackResponsesData) result.getOutput();
        FeedbackResponseData actualResponse = responses.getResponses().get(0);

        FeedbackResponseAttributes expectedResponse = logic.getFeedbackResponse(data.question.getId(),
                data.instructor.getEmail(), data.instructor.getEmail());
        expectedResponse.setResponseDetails(responseDetails);

        ______TS("Successful instructor submission; should produce valid result.");

        verifyFeedbackResponseEquals(expectedResponse, actualResponse);
        logoutUser();
    }

    @Test
    public void testExecute_feedbackQuestionDoesNotExist_shouldThrowEntityNotFoundException() throws Exception {
        String questionNumber = "999";
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionNumber,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("Question does not exist; should throw exception.");

        verifyEntityNotFound(submissionParams);
    }

    @Test
    public void testExecute_intentIsUnknown_shouldThrowInvalidHttpParameterException() throws Exception {
        TestData data = new TestData(1, "session1InCourse1");
        String[] submissionParams = getParams(data.question, Intent.STUDENT_RESULT);

        ______TS("Intent is unknown; should throw exception.");

        verifyHttpParameterFailure(submissionParams);
    }

    @Test
    public void testExecute_recipientIsNotValid_shouldThrowInvalidOperationException() throws Exception {
        String invalidRecipient = "invalid email";
        TestData data = dataWithStudent(1, "session1InCourse1", "student1InCourse1");

        FeedbackTextResponseDetails responseDetails = new FeedbackTextResponseDetails("Updated response details");
        var responsesRequest = createSingletonResponsesRequest(invalidRecipient, responseDetails);

        loginAsStudent(data.student.getGoogleId());
        String[] submissionParams = getParams(data.question, Intent.STUDENT_SUBMISSION);

        ______TS("Recipient is invalid; should throw exception.");

        verifyInvalidOperation(responsesRequest, submissionParams);
        logoutUser();
    }

    @Test
    public void testAccessControl_instructorSubmissionPastEndTime_shouldAllowIfBeforeDeadline() throws Exception {
        TestData data = dataWithInstructor(4, "session1InCourse1", "instructor1OfCourse1");

        Instant newEndTime = TimeHelper.getInstantDaysOffsetFromNow(-2);
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(data.sessionName, data.courseId)
                .withInstructorDeadlines(Map.of())
                .withEndTime(newEndTime)
                .build());
        loginAsInstructor(data.instructor.getGoogleId());
        String[] submissionParams = getParams(data.question, Intent.INSTRUCTOR_SUBMISSION);

        ______TS("No selective deadline; should fail.");

        verifyCannotAccess(submissionParams);

        ______TS("After selective deadline; should fail.");

        Map<String, Instant> newInstructorDeadlines = Map.of(
                data.instructor.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(-1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(data.sessionName, data.courseId)
                .withInstructorDeadlines(newInstructorDeadlines)
                .build());
        verifyCannotAccess(submissionParams);

        ______TS("Before selective deadline; should pass.");

        newInstructorDeadlines = Map.of(
                data.instructor.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(data.sessionName, data.courseId)
                .withInstructorDeadlines(newInstructorDeadlines)
                .build());
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionPastEndTime_shouldAllowIfBeforeDeadline() throws Exception {
        TestData data = dataWithStudent(1, "session1InCourse1", "student1InCourse1");

        Instant newEndTime = TimeHelper.getInstantDaysOffsetFromNow(-2);
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(data.sessionName, data.courseId)
                .withEndTime(newEndTime)
                .build());
        loginAsStudent(data.student.getGoogleId());
        String[] submissionParams = getParams(data.question, Intent.STUDENT_SUBMISSION);

        ______TS("No selective deadline; should fail.");

        verifyCannotAccess(submissionParams);

        ______TS("After selective deadline; should fail.");

        Map<String, Instant> newStudentDeadlines = Map.of(
                data.student.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(-1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(data.sessionName, data.courseId)
                .withStudentDeadlines(newStudentDeadlines)
                .build());
        verifyCannotAccess(submissionParams);

        ______TS("Before selective deadline; should pass.");

        newStudentDeadlines = Map.of(
                data.student.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(data.sessionName, data.courseId)
                .withStudentDeadlines(newStudentDeadlines)
                .build());
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_feedbackQuestionDoesNotExist_shouldThrowEntityNotFoundException() throws Exception {
        String questionNumber = "999";
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionNumber,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("Question does not exist; should throw exception.");

        verifyEntityNotFoundAcl(submissionParams);
    }

    @Test
    public void testAccessControl_studentDoesNotExist_shouldThrowEntityNotFoundException() throws Exception {
        TestData data = new TestData(1, "sessionInTestingWithoutStudent");

        String[] submissionParams = getParams(data.question, Intent.STUDENT_SUBMISSION);

        ______TS("Student does not exist; should throw exception.");

        verifyEntityNotFoundAcl(submissionParams);
    }

    @Test
    public void testAccessControl_instructorDoesNotExist_shouldThrowEntityNotFoundException() throws Exception {
        TestData data = new TestData(1, "sessionInTestingWithoutInstructor");
        String[] submissionParams = getParams(data.question, Intent.INSTRUCTOR_SUBMISSION);

        ______TS("Instructor does not exist; should throw exception.");

        verifyEntityNotFoundAcl(submissionParams);
    }

    @Test
    public void testAccessControl_unknownIntent_shouldThrowInvalidHttpParameterException() throws Exception {
        TestData data = new TestData(4, "session1InCourse1");

        String[] submissionParams = getParams(data.question, Intent.FULL_DETAIL);

        ______TS("Incorrect intent parameter; should throw exception.");

        verifyHttpParameterFailureAcl(submissionParams);
    }

    @Test
    public void testAccessControl_studentResultIntent_shouldThrowInvalidHttpParameterException() throws Exception {
        TestData data = new TestData(4, "session1InCourse1");

        String[] submissionParams = getParams(data.question, Intent.STUDENT_RESULT);

        ______TS("Student Result Intent; should throw exception.");

        verifyHttpParameterFailureAcl(submissionParams);
    }

    @Test
    public void testAccessControl_instructorResultIntent_shouldThrowInvalidHttpParameterException() throws Exception {
        TestData data = new TestData(4, "session1InCourse1");

        String[] submissionParams = getParams(data.question, Intent.INSTRUCTOR_RESULT);

        ______TS("Incorrect intent parameter; should throw exception.");

        verifyHttpParameterFailureAcl(submissionParams);
    }

    private void verifyFeedbackResponseEquals(FeedbackResponseAttributes expected, FeedbackResponseData actual)
            throws Exception {
        assertEquals(expected.getId(), StringHelper.decrypt(actual.getFeedbackResponseId()));
        assertEquals(expected.getGiver(), actual.getGiverIdentifier());
        assertEquals(expected.getRecipient(), actual.getRecipientIdentifier());
        assertEquals(expected.getResponseDetailsCopy().getAnswerString(), actual.getResponseDetails().getAnswerString());
        assertEquals(expected.getResponseDetailsCopy().getQuestionType(), actual.getResponseDetails().getQuestionType());
        assertEquals(JsonUtils.toJson(expected.getResponseDetailsCopy()),
                JsonUtils.toJson(actual.getResponseDetails()));
    }

    /**
     * Get the submission parameters for a given question and intent.
     */
    private String[] getParams(FeedbackQuestionAttributes question, Intent intent) {
        return new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.INTENT, intent.toString(),
        };
    }

    /**
     * Create a {@code FeedbackResponsesRequest} with a single response defined by {@code details}.
     */
    private FeedbackResponsesRequest createSingletonResponsesRequest(String recipient, FeedbackResponseDetails details) {
        FeedbackResponsesRequest responsesRequest = new FeedbackResponsesRequest();
        var responseRequest = new FeedbackResponsesRequest.FeedbackResponseRequest(
                recipient, details);
        responsesRequest.setResponses(Collections.singletonList(responseRequest));
        return responsesRequest;
    }

    /**
     * Create a {@code TestData} instance for a given student.
     */
    private TestData dataWithStudent(int questionNumber, String session, String student) {
        TestData data = new TestData(questionNumber, session);
        data.student = typicalBundle.students.get(student);
        return data;
    }

    /**
     * Create a {@code TestData} instance for a given student with a specified bundle.
     */
    private TestData dataWithStudent(int questionNumber, String session, String student, DataBundle bundle) {
        TestData data = new TestData(questionNumber, session);
        data.student = bundle.students.get(student);
        return data;
    }

    /**
     * Create a {@code TestData} instance for a given instructor.
     */
    private TestData dataWithInstructor(int questionNumber, String session, String instructor) {
        TestData data = new TestData(questionNumber, session);
        data.instructor = typicalBundle.instructors.get(instructor);
        return data;
    }

    /**
     * Create a {@code TestData} instance for a given instructor with a specified bundle.
     */
    private TestData dataWithInstructor(int questionNumber, String session, String instructor, DataBundle bundle) {
        TestData data = new TestData(questionNumber, session);
        data.instructor = bundle.instructors.get(instructor);
        return data;
    }

    /**
     * Helper class to more easily handle neccessary test data.
     */
    private class TestData {
        int questionNumber;
        FeedbackSessionAttributes session;
        String sessionName;
        String courseId;
        FeedbackQuestionAttributes question;
        StudentAttributes student;
        InstructorAttributes instructor;

        TestData(int questionNumber, String session) {
            this.questionNumber = questionNumber;
            this.session = typicalBundle.feedbackSessions.get(session);
            sessionName = this.session.getFeedbackSessionName();
            courseId = this.session.getCourseId();
            question = logic.getFeedbackQuestion(sessionName, courseId, questionNumber);
        }
    }
}
