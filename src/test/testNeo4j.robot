*** Settings ***
Library    Collections
Library    RequestsLibrary
Test Timeout    30 seconds
Test Setup    Create Session    localhost    http://localhost:8080

*** Test Cases ***
addActorCorrectFormat
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    name=bob    actorId=nm7001850
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

addActorIncorrectFormat
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    name=Alice    movieId=nm1111111111111111111111111111111
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400
