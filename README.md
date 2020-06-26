# Task candidates Rest API Extension

[![GitHub version](https://badge.fury.io/gh/Bonitasoft-Community%2task-candidates-rest-api.svg)](https://badge.fury.io/gh/Bonitasoft-Community%2Ftask-candidates-rest-api)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=Bonitasoft-Community_task-candidates-rest-api&metric=alert_status)](https://sonarcloud.io/dashboard?id=Bonitasoft-Community_task-candidates-rest-api)

Get the lsit of users that can performed a given task

## Build

Run `./mvnw`

## Usage

1. Install `target/task-candidates-rest-api-1.0.0-<version>.zip` in your tenant `Resources` using Bonita Admininstration Portal
1. Call the API extension using `../API/extension/task/candidate?p=0&c=10&taskId=1001` where `p` and `c` are pagination parameters and `taskId` the task instance id
1. It will return a json array of `userId`: `userName` objects:
```json
	[
	{ 15:"walter.bates" },
	{ 32:"helen.kelly" }
	]
```
1. `task_visualization` permission is used for this API