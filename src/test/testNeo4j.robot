*** Settings ***
Library    Collections
Library    RequestsLibrary
Test Timeout    30 seconds
Suit Setup    Create Session    localhost    http://localhost:8080

*** Test Cases ***
addActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    name=bob    actorId=nm7001850
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200
