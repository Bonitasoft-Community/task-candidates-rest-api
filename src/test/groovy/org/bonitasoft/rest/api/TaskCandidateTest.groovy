package org.bonitasoft.rest.api;

import groovy.json.JsonSlurper

import javax.servlet.http.HttpServletRequest

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.api.ProcessAPI
import org.bonitasoft.engine.identity.User
import org.bonitasoft.engine.search.Order
import org.bonitasoft.engine.search.SearchResult
import org.bonitasoft.engine.search.impl.SearchResultImpl
import org.bonitasoft.engine.session.APISession
import org.bonitasoft.web.extension.ResourceProvider
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder

import spock.lang.Specification

import org.bonitasoft.web.extension.rest.RestAPIContext

/**
 * @see http://spockframework.github.io/spock/docs
 */
class TaskCandidateTest extends Specification {

    // Declare mocks here
    // Mocks are used to simulate external dependencies behavior
    def HttpServletRequest httpRequest = Mock(HttpServletRequest)
    def RestAPIContext context = Mock(RestAPIContext)
    def APIClient apiClient = Mock(APIClient)
    def ProcessAPI processAPI = Mock(ProcessAPI)

    /**
     * You can configure mocks before each tests in the setup method
     */
    def setup(){
        httpRequest.getParameter("p") >> "0"
        httpRequest.getParameter('taskId') >> "1"
        httpRequest.getParameter("c") >> "10"

        context.apiClient >> apiClient
        apiClient.processAPI >> processAPI
    }

    def should_validate_p_parameter_is_mandatoty() {
        when: "Invoking the TaskCandidate API"
        def apiResponse = new TaskCandidate().doHandle(httpRequest, new RestApiResponseBuilder(), context)

        then: "A Bad Request response is returned when p is null"
        httpRequest.getParameter("p") >> null
        apiResponse.httpStatus == 400
    }

    def should_return_a_bad_request_when_p_parameter_is_not_an_integer() {
        when: "Invoking the TaskCandidate API"
        def apiResponse = new TaskCandidate().doHandle(httpRequest, new RestApiResponseBuilder(), context)

        then: "A Bad Request response is returned when p is not an integer"
        httpRequest.getParameter("p") >> "not a number"
        apiResponse.httpStatus == 400
    }

    def should_validate_p_parameter_is_an_integer() {
        when: "Parsing request parameters"
        def parameters = new TaskCandidate().parseRequestParameters(httpRequest)

        then: "p is cast into an integer"
        httpRequest.getParameter("p") >> "1"
        parameters.p == 1
    }

    def should_validate_c_parameter_is_mandatoty() {
        when: "Invoking the TaskCandidate API"
        def apiResponse = new TaskCandidate().doHandle(httpRequest, new RestApiResponseBuilder(), context)

        then: "A Bad Request response is returned when c is null"
        httpRequest.getParameter("c") >> null
        apiResponse.httpStatus == 400
    }

    def should_return_a_bad_request_when_c_parameter_is_not_an_integer() {
        when: "Invoking the TaskCandidate API"
        def apiResponse = new TaskCandidate().doHandle(httpRequest, new RestApiResponseBuilder(), context)

        then: "A Bad Request response is returned when c is not an integer"
        httpRequest.getParameter("c") >> "not a number"
        apiResponse.httpStatus == 400
    }

    def should_validate_c_parameter_is_an_integer() {
        when: "Parsing request parameters"
        def parameters = new TaskCandidate().parseRequestParameters(httpRequest)

        then: "c is cast to an integer "
        httpRequest.getParameter("c") >> "10"
        parameters.c == 10
    }

    def should_validate_taskId_parameter_is_mandatoty() {
        when: "Invoking the TaskCandidate API"
        def apiResponse = new TaskCandidate().doHandle(httpRequest, new RestApiResponseBuilder(), context)

        then: "A Bad Request response is returned when taskId is null"
        httpRequest.getParameter("taskId") >> null
        apiResponse.httpStatus == 400
    }

    def should_return_a_bad_request_when_taskId_parameter_is_not_a_long() {
        when: "Invoking the TaskCandidate API"
        def apiResponse = new TaskCandidate().doHandle(httpRequest, new RestApiResponseBuilder(), context)

        then: "A Bad Request response is returned when taskId is not a long"
        httpRequest.getParameter("taskId") >> "not a number"
        apiResponse.httpStatus == 400
    }

    def should_validate_taskId_parameter_is_a_long() {
        when: "Parsing request parameters"
        def parameters = new TaskCandidate().parseRequestParameters(httpRequest)

        then: "taskId is cast into a long"
        httpRequest.getParameter('taskId') >> "1001"
        parameters.taskId == 1001L
    }
    
    def return_a_bad_request_when_o_parameter_is_not_a_user_descriptor() {
        when: "Invoking the TaskCandidate API"
        def apiResponse = new TaskCandidate().doHandle(httpRequest, new RestApiResponseBuilder(), context)

        then: "A Bad Request response is returned when o is not a user search descriptor"
        httpRequest.getParameter('o') >> "birthDate"
        apiResponse.httpStatus == 400
    }
    
    def should_validate_o_parameter_is_a_user_search_descriptor() {
         when: "Parsing request parameters"
        def parameters = new TaskCandidate().parseRequestParameters(httpRequest)

        then: "userName is the order descriptor"
        httpRequest.getParameter('o') >> "userName DESC"
        parameters.orderBy == "userName"
        parameters.order == Order.DESC
    }
    

    def should_return_users_who_can_execute_the_task() {
        when: "Invoking the TaskCandidate API"
        def result = new TaskCandidate().doHandle(httpRequest, new RestApiResponseBuilder(), context)

        then: "The lsit of candidates is returned"
        processAPI.searchUsersWhoCanExecutePendingHumanTask(1L, _) >> Stub(SearchResult){
           it.count >> 2
           it.result >> [
                Stub(User){
                   it.id >> 1
                   it.userName >> 'romain'
                },
                Stub(User){
                    it.id >> 2
                    it.userName >> 'adrien'
                }
            ]
        }
        result.response == """[{"id":1,"username":"romain"},{"id":2,"username":"adrien"}]"""
        result.httpStatus == 200
    }

}