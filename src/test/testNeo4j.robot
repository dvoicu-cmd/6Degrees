*** Settings ***
Library    Collections
Library    RequestsLibrary
Test Timeout    30 seconds
Test Setup    Create Session    localhost    http://localhost:8080

*** Test Cases ***

##### PUT REQUESTS #####

### addActor Tests ###

addActorCorrectFormat
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    name=bob    actorId=nm7001850
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

addActorIncorrectFormat
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    name=Alice    movieId=nm1111111111111111111111111111111
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

### addMovie Tests ###

addMovieCorrectFormat
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    name=Wall-E    movieId=nm10385
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

addMovieIncorrectFormat
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    name=DriverLiterallyMe    actorId=nm111111111113333333333333333333333333333333
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

addMovieDupe
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params1}=   Create Dictionary    name=Oppenheimer    movieId=nm48123
    ${params2}=   Create Dictionary    name=Barbie    movieId=nm48123
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params1}    headers=${headers}    expected_status=200
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params2}    headers=${headers}    expected_status=400

### addRelationship Tests ###

addRelationshipPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    actorId=nm7001850    movieId=nm10385    #Bob acted in Wall-E
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

addRelationshipDuplicate
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    actorId=nm7001850    movieId=nm10385    #Bob acted in Wall-E
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

addRelationshipDuplicate2
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params1}=   Create Dictionary    actorId=nm7001850    movieId=nm48123    #Bob acted in Wall-E
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params1}    headers=${headers}    expected_status=200
    ${params2}=   Create Dictionary    movieId=nm48123    actorId=nm7001850    #Bob acted in Wall-E
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params2}    headers=${headers}    expected_status=400


##### GET REQUESTS #####

getActor
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params1}=    Create Dictionary    name=Bill    actorId=nm1111891
    ${resp1}=    PUT On Session    localhost    /api/v1/addActor    json=${params1}    headers=${headers}    expected_status=200
    ${resp2}=    GET On Session    localhost    /api/v1/getActor?actorId=nm1111891    headers=${headers}    expected_status=200
    Should Be Equal As Strings    ${params1}    ${resp2.json()}
