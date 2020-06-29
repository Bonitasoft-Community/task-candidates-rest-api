package org.bonitasoft.rest.api;

import groovy.json.JsonBuilder

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpHeaders
import org.bonitasoft.engine.bpm.process.impl.CatchErrorEventTiggerDefinitionBuilder
import org.bonitasoft.engine.identity.User
import org.bonitasoft.engine.identity.UserSearchDescriptor
import org.bonitasoft.engine.search.Order
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
    
    def static final USER_DESCRIPTORS = [
        UserSearchDescriptor.ID,
        UserSearchDescriptor.USER_NAME,
        UserSearchDescriptor.LAST_NAME,
        UserSearchDescriptor.FIRST_NAME,
        UserSearchDescriptor.GROUP_ID,
        UserSearchDescriptor.ROLE_ID,
        UserSearchDescriptor.MANAGER_USER_ID,
        UserSearchDescriptor.ENABLED,
        UserSearchDescriptor.LAST_CONNECTION
    ]

    @Override
    RestApiResponse doHandle(HttpServletRequest request, RestApiResponseBuilder responseBuilder, RestAPIContext context) {
		try {
			def requestParameters = parseRequestParameters(request)
			def processAPI = context.apiClient.processAPI
            def searchOptionsBuilder = new SearchOptionsBuilder(requestParameters.p, requestParameters.c)
                                            .filter(UserSearchDescriptor.ENABLED, true)
            if(requestParameters.orderBy) {
                searchOptionsBuilder.sort(requestParameters.orderBy, requestParameters.order ?: Order.ASC_NULLS_LAST)
            }
			def searchResult = processAPI.searchUsersWhoCanExecutePendingHumanTask(requestParameters.taskId, searchOptionsBuilder.done())
			return buildPagedResponse(responseBuilder,
				 new JsonBuilder(searchResult.result.collect { User user -> [
                     id:user.id, 
                     userName:user.userName,
                     lastName:user.lastName,
                     firstName:user.firstName,
                     jobTitle:user.jobTitle,
                     lastConnection:user.lastConnection,
                     lastUpdate:user.lastUpdate,
                     enabled:user.enabled,
                     createdBy:user.createdBy,
                     managerUserId:user.managerUserId,
                     creationDate:user.creationDate,
                     iconId:user.iconId,
                     title:user.title
                     ] }).toString(),
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
        def o = request.getParameter "o"
        if (o) {
            def orderParam = o.split(" ")
            def orderBy = orderParam[0]
            if(!USER_DESCRIPTORS.contains(orderBy)) {
                throw new IllegalArgumentException("'$orderBy' is not a valid order descriptor")
            }
            def order = Order.ASC_NULLS_LAST
            if(orderParam.length > 1) {
                order = Order.valueOf(orderParam[1])
            }
            requestParameters.put('orderBy',orderBy)
            requestParameters.put('order', order)
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
