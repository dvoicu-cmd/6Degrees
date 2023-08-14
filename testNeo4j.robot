*** Settings ***
Library    Collections
Library    RequestsLibrary
Test Timeout    30 seconds
Test Setup    Create Session    localhost    http://localhost:8080

*** Test Cases ***
getMovieFail2  #Inproper format
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${url}=    Set Variable    /api/v1/getMovie?moaeertjdkjhuiiuiuhId=nAm111111111111111111111111891
    ${resp2}=    GET On Session    localhost    url=${url}    headers=${headers}    expected_status=400

### hasRelationship Tests ###
