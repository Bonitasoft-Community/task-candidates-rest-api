# Task candidates Rest API Extension

![](https://github.com/Bonitasoft-Community/task-candidates-rest-api/workflows/Build/badge.svg)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=Bonitasoft-Community_task-candidates-rest-api&metric=alert_status)](https://sonarcloud.io/dashboard?id=Bonitasoft-Community_task-candidates-rest-api)
[![GitHub version](https://badge.fury.io/gh/Bonitasoft-Community%2Ftask-candidates-rest-api.svg)](https://badge.fury.io/gh/Bonitasoft-Community%2Ftask-candidates-rest-api)

Get the list of users that can performed a given task. Only `enabled` users are returned.

## Build

Run `./mvnw`

## Usage

1. Install `target/task-candidates-rest-api-1.0.0-<version>.zip` in your tenant `Resources` using Bonita Admininstration Portal
1. Call the API extension using `../API/extension/task/candidate?p=0&c=10&taskId=1001` where `p` and `c` are pagination parameters and `taskId` the task instance id
1. It will return a json array of `user` objects:
	
	```json
	[
		{
			"id" : 15,
			"userName": "helen.kelly",
			"lastName": "Kelly",
			"firstName": "Helen",
			"title": "Mrs.",
			"jobTitle": "HR manager",
			"lastConnection": "2020-06-29T08:54:23+0000",
			"lastUpdate": "2020-01-02T13:54:23+0000",
			"enabled": true,
			"createdBy": -1,
			"managerUserId": 21,
			"creationDate": "2019-01-02T13:54:23+0000",
			"iconId: 123
		}
	]
	```	
1. `o=userName ASC` can be used to sort the response array by userName, see [UserSearchDescriptor](https://documentation.bonitasoft.com/javadoc/api/7.10.5/org/bonitasoft/engine/identity/UserSearchDescriptor.html) for compatible sort descriptors.
1. `task_visualization` permission is used for this API
