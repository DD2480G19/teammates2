package teammates.ui.webapi;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.FeedbackResponsesRequest;
import teammates.ui.request.Intent;
import teammates.ui.request.InvalidHttpRequestBodyException;

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
        int questionNumber = 1;
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session.getFeedbackSessionName();
        String courseId = session.getCourseId();
        StudentAttributes student = typicalBundle.students.get("student2InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);

        loginAsStudent(student.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        String existingAnswer = typicalBundle.feedbackResponses
                .get("response2ForQ1S1C1").getResponseDetails().getAnswerString();

        var existingResponses = logic.getFeedbackResponsesFromStudentOrTeamForQuestion(qn1InSession1InCourse1, student);
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
        int questionNumber = 1;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);

        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        FeedbackResponsesRequest responsesRequest = new FeedbackResponsesRequest();
        FeedbackTextResponseDetails responseDetails = new FeedbackTextResponseDetails("Updated response details");
        FeedbackResponsesRequest.FeedbackResponseRequest responseRequest = new FeedbackResponsesRequest
                .FeedbackResponseRequest(student1InCourse1.getEmail(), responseDetails);

        responsesRequest.setResponses(Collections.singletonList(responseRequest));

        SubmitFeedbackResponsesAction a = getAction(responsesRequest, submissionParams);
        JsonResult result = getJsonResult(a);
        FeedbackResponsesData responses = (FeedbackResponsesData) result.getOutput();
        FeedbackResponseData actualResponse = responses.getResponses().get(0);

        FeedbackResponseAttributes expectedResponse = logic.getFeedbackResponse(qn1InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student1InCourse1.getEmail());
        expectedResponse.setResponseDetails(responseDetails);

        ______TS("Successful student submission; should produce valid result.");

        verifyFeedbackResponseEquals(expectedResponse, actualResponse);
        logoutUser();
    }

    @Test
    public void testExecute_instructorSubmission_shouldProduceValidResult() throws Exception {
        int questionNumber = 1;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        InstructorAttributes instructor1InCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);

        loginAsInstructor(instructor1InCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        FeedbackResponsesRequest responsesRequest = new FeedbackResponsesRequest();
        FeedbackTextResponseDetails responseDetails = new FeedbackTextResponseDetails("Updated response details");
        FeedbackResponsesRequest.FeedbackResponseRequest responseRequest = new FeedbackResponsesRequest
                .FeedbackResponseRequest(instructor1InCourse1.getEmail(), responseDetails);

        responsesRequest.setResponses(Collections.singletonList(responseRequest));

        SubmitFeedbackResponsesAction a = getAction(responsesRequest, submissionParams);
        JsonResult result = getJsonResult(a);
        FeedbackResponsesData responses = (FeedbackResponsesData) result.getOutput();
        FeedbackResponseData actualResponse = responses.getResponses().get(0);

        FeedbackResponseAttributes expectedResponse = logic.getFeedbackResponse(qn1InSession1InCourse1.getId(),
                instructor1InCourse1.getEmail(), instructor1InCourse1.getEmail());
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
        int questionNumber = 1;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };

        ______TS("Intent is unknown; should throw exception.");

        verifyHttpParameterFailure(submissionParams);
    }

    @Test
    public void testExecute_recipientIsNotValid_shouldThrowInvalidOperationException() throws Exception {
        String invalidRecipient = "invalid email";
        int questionNumber = 1;
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        String feedbackSessionName = session1InCourse1.getFeedbackSessionName();
        String courseId = session1InCourse1.getCourseId();
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);
        FeedbackResponsesRequest responsesRequest = new FeedbackResponsesRequest();
        FeedbackTextResponseDetails responseDetails = new FeedbackTextResponseDetails("Updated response details");
        FeedbackResponsesRequest.FeedbackResponseRequest responseRequest =
                new FeedbackResponsesRequest.FeedbackResponseRequest(invalidRecipient, responseDetails);
        responsesRequest.setResponses(Collections.singletonList(responseRequest));

        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("Recipient is invalid; should throw exception.");

        verifyInvalidOperation(responsesRequest, submissionParams);
        logoutUser();
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
        int questionNumber = 1;
        FeedbackSessionAttributes sessionInTestingWithoutStudent = typicalBundle.feedbackSessions
                .get("sessionInTestingWithoutStudent");
        String feedbackSessionName = sessionInTestingWithoutStudent.getFeedbackSessionName();
        String courseId = sessionInTestingWithoutStudent.getCourseId();
        FeedbackQuestionAttributes qn1InSessionInTestingWithoutStudent = logic.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSessionInTestingWithoutStudent.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
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
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
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
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
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

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn4InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
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
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        ______TS("Incorrect intent parameter; should throw exception.");

        verifyHttpParameterFailureAcl(submissionParams);
    }

    @Test
    public void testExecute_invalidResponse_shouldThrowInvalidHttpRequestBodyException() throws Exception {
        int questionNumber = 6;
        var session = typicalBundle.feedbackSessions.get("session1InCourse1");
        var student = typicalBundle.students.get("student1InCourse1");
        // Responses to this question should be in the range 1 <= x <= 5 with a step size of 0.5
        var question = logic.getFeedbackQuestion(session.getFeedbackSessionName(), session.getCourseId(), questionNumber);
        //var question = typicalBundle.feedbackQuestions.get("qn6InSession1InCourse1");
        loginAsStudent(student.getGoogleId());

        //var existingResponses = logic.getFeedbackResponsesFromStudentOrTeamForQuestion(question, student);
        var badDetails = new FeedbackNumericalScaleResponseDetails();
        badDetails.setAnswer(0.9);
        var newResponses = Collections.singletonList(
                FeedbackResponseAttributes.builder(question.getId(), student.getEmail(), student.getEmail())
                        .withCourseId(session.getCourseId())
                        .withFeedbackSessionName(session.getFeedbackSessionName())
                        .withResponseDetails(badDetails)
                        .build()
        );
        var request = feedbackAttributesToRequest(newResponses);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        
        SubmitFeedbackResponsesAction a = getAction(request, submissionParams);

        ______TS("Valid responses should be in range [1, 5] with step size .5; new response is 0.9");
        assertThrows(InvalidHttpRequestBodyException.class, () -> a.execute());
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

    private FeedbackResponsesRequest feedbackAttributesToRequest(List<FeedbackResponseAttributes> responses) {
        // Create a responses requestion with all existing responses
        FeedbackResponsesRequest responsesRequest = new FeedbackResponsesRequest();
        responsesRequest.setResponses(responses.stream().map(r ->
                new FeedbackResponsesRequest.FeedbackResponseRequest(
                        r.getRecipient(),
                        r.getResponseDetails()
        )).collect(Collectors.toList()));
        return responsesRequest;
    }
}
