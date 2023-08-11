*** Settings ***
Library    Collections
Library    RequestsLibrary
Test Timeout    30 seconds
Test Setup    Create Session    localhost    http://localhost:8080

*** Test Cases ***

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