*** Settings ***
Library    Collections
Library    RequestsLibrary
Test Timeout    30 seconds
Test Setup    Create Session    localhost    http://localhost:8080

### NOTE: The tests being executed inorder is important since some later tests use the nodes created in earlier tests


*** Test Cases ***

computeBaconNumberPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${paramsExpected0}=    Evaluate    {"baconNumber": 0}
    ${paramsExpected1}=    Evaluate    {"baconNumber": 1}
    ${paramsExpected2}=    Evaluate    {"baconNumber": 2}

    #Execute GET
    ${result0}=    GET On Session    localhost    url=/api/v1/computeBaconNumber?actorId=nm0000102    headers=${headers}    expected_status=200
    ${result1}=    GET On Session    localhost    url=/api/v1/computeBaconNumber?actorId=nm654321    headers=${headers}    expected_status=200
    ${result2}=    GET On Session    localhost    url=/api/v1/computeBaconNumber?actorId=nm7001850    headers=${headers}    expected_status=200

    ${result0_dict}=    Evaluate    json.loads('''${result0.text}''')
    Should Be Equal    ${paramsExpected0}    ${result0_dict}

    ${result1_dict}=    Evaluate    json.loads('''${result1.text}''')
    Should Be Equal    ${paramsExpected1}    ${result1_dict}

    ${result2_dict}=    Evaluate    json.loads('''${result2.text}''')
    Should Be Equal    ${paramsExpected2}    ${result2_dict}