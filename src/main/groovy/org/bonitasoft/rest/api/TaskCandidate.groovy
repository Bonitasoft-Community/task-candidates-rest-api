package org.bonitasoft.rest.api;

import groovy.json.JsonBuilder

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpHeaders
import org.bonitasoft.engine.bpm.process.impl.CatchErrorEventTiggerDefinitionBuilder
import org.bonitasoft.engine.identity.User
import org.bonitasoft.engine.identity.UserSearchDescriptor
import org.bonitasoft.engine.search.SearchOptionsBuilder
import org.bonitasoft.web.extension.ResourceProvider
import org.bonitasoft.web.extension.rest.RestApiResponse
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.bonitasoft.web.extension.rest.RestAPIContext
import org.bonitasoft.web.extension.rest.RestApiController

class TaskCandidate implements RestApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCandidate.class)

    @Override
    RestApiResponse doHandle(HttpServletRequest request, RestApiResponseBuilder responseBuilder, RestAPIContext context) {
		try {
			def requestParameters = parseRequestParameters(request)
			def processAPI = context.apiClient.processAPI
			def searchResult = processAPI.searchUsersWhoCanExecutePendingHumanTask(requestParameters.taskId, new SearchOptionsBuilder(requestParameters.p, requestParameters.c)
				.filter(UserSearchDescriptor.ENABLED, true)
				.done())
			return buildPagedResponse(responseBuilder,
				 new JsonBuilder(searchResult.result.collect { User user -> [(user.id):user.userName] }).toString(),
				requestParameters.p,
				requestParameters.c, 
				searchResult.count)
		}catch(IllegalArgumentException e ) {
			return buildResponse(responseBuilder, HttpServletResponse.SC_BAD_REQUEST, """{"error":"${e.message}"}""")
		}
    }
	
	def parseRequestParameters(HttpServletRequest request) {
		def requestParameters = [:]
		def p = request.getParameter "p"
		if (p == null) {
			throw new IllegalArgumentException('The parameter p is missing')
        }
		try {
			requestParameters.put("p",p.toInteger())
		}catch(NumberFormatException nbe) {
			throw new IllegalArgumentException('The parameter p must be an integer value')
		}
		
		def c = request.getParameter "c"
		if (c == null) {
			throw new IllegalArgumentException('The parameter c is missing')
		}
		try {
			requestParameters.put("c",c.toInteger())
		}catch(NumberFormatException nbe) {
			throw new IllegalArgumentException('The parameter c must be a integer value')
		}
		
		def taskId = request.getParameter "taskId"
		if (taskId == null) {
			throw new IllegalArgumentException('The parameter taskId is missing')
		}
		try {
			requestParameters.put("taskId", taskId.toLong())
		}catch(NumberFormatException nbe) {
			throw new IllegalArgumentException('The parameter taskId must be a long value')
		}
		return requestParameters
	}

    /**
     * Build an HTTP response.
     *
     * @param  responseBuilder the Rest API response builder
     * @param  httpStatus the status of the response
     * @param  body the response body
     * @return a RestAPIResponse
     */
    RestApiResponse buildResponse(RestApiResponseBuilder responseBuilder, int httpStatus, Serializable body) {
        return responseBuilder.with {
            withResponseStatus(httpStatus)
            withResponse(body)
            build()
        }
    }

    /**
     * Returns a paged result like Bonita BPM REST APIs.
     * Build a response with a content-range.
     *
     * @param  responseBuilder the Rest API response builder
     * @param  body the response body
     * @param  p the page index
     * @param  c the number of result per page
     * @param  total the total number of results
     * @return a RestAPIResponse
     */
    RestApiResponse buildPagedResponse(RestApiResponseBuilder responseBuilder, Serializable body, int p, int c, long total) {
        return responseBuilder.with {
            withContentRange(p,c,total)
            withResponse(body)
            build()
        }
    }

    
}
