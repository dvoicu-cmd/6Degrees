*** Settings ***
Library    Collections
Library    RequestsLibrary
Test Timeout    30 seconds
Test Setup    Create Session    localhost    http://localhost:8080

### NOTE: The tests being executed inorder is important since some later tests use the nodes created in earlier tests


*** Test Cases ***
##### PUT REQUESTS #####

### addActor Tests ###
addActorCorrectPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    name=bob    actorId=nm7001850
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

addActorFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    name=Alice    movieId=nm1111111111111111111111111111111
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

### addMovie Tests ###

addMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    name=Wall-E    movieId=nm10385
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

addMovieFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    name=DriverLiterallyMe    actorId=nm111111111113333333333333333333333333333333
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

addMovieFail2
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

addRelationshipFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=   Create Dictionary    actorId=nm7001850    movieId=nm10385    #Bob acted in Wall-E
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=400

addRelationshipFail2
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params1}=   Create Dictionary    actorId=nm7001850    movieId=nm48123    #Bob acted in Wall-E
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params1}    headers=${headers}    expected_status=200
    ${params2}=   Create Dictionary    movieId=nm48123    actorId=nm7001850    #Bob acted in Wall-E
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params2}    headers=${headers}    expected_status=400

##### GET REQUESTS #####

### getActor Tests ###

getActorPass
    #Add the movie/actor
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params1}=    Create Dictionary    name=Bill    actorId=nm1111891
    ${paramsExpected}=    Evaluate    {"name": "Bill", "actorId": "nm1111891", "movies": []}
    ${resp1}=    PUT On Session    localhost    /api/v1/addActor    json=${params1}    headers=${headers}    expected_status=200

    #Execute GET
    ${url}=    Set Variable    /api/v1/getActor?actorId=nm1111891
    ${resp2}=    GET On Session    localhost    url=${url}    headers=${headers}    expected_status=200

    #Evaluate if result is correct
    ${resp2_dict}=    Evaluate    json.loads('''${resp2.text}''')
    Should Be Equal    ${paramsExpected}    ${resp2_dict}

getActorFail    #Actor does not exist
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${url}=    Set Variable    /api/v1/getActor?actorId=naa3344555m11118123gb91
    ${resp2}=    GET On Session    localhost    url=${url}    headers=${headers}    expected_status=404

getActorFail2  #Inproper format
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${url}=    Set Variable    /api/v1/getActor?movieId=nAm111111111111111111111111891
    ${resp2}=    GET On Session    localhost    url=${url}    headers=${headers}    expected_status=400

### getMovie Tests ###

getMoviePass
    #Add the movie/actor
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params1}=    Create Dictionary    name=Spider-Man    movieId=nm01345
    ${paramsExpected}=    Evaluate    {"name": "Spider-Man", "movieId": "nm01345", "actors": []}
    ${resp1}=    PUT On Session    localhost    /api/v1/addMovie    json=${params1}    headers=${headers}    expected_status=200

    #Execute GET
    ${url}=    Set Variable    /api/v1/getMovie?movieId=nm01345
    ${resp2}=    GET On Session    localhost    url=${url}    headers=${headers}    expected_status=200

    #Evaluate if result is correct
    ${resp2_dict}=    Evaluate    json.loads('''${resp2.text}''')
    Should Be Equal    ${paramsExpected}    ${resp2_dict}

getMovieFail    #Movie does not exist
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${url}=    Set Variable    /api/v1/getMovie?movieId=nnnnnnnmmmmmmmmmm1111111111111111111
    ${resp2}=    GET On Session    localhost    url=${url}    headers=${headers}    expected_status=404

getMovieFail2  #Inproper format
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${url}=    Set Variable    /api/v1/getMovie?moaeertjdkjhuiiuiuhId=nAm111111111111111111111111891
    ${resp2}=    GET On Session    localhost    url=${url}    headers=${headers}    expected_status=400

### hasRelationship Tests ###

hasRelationshipPass
    #Add the movie and actor
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params1}=    Create Dictionary    name=Men    movieId=nm0123456
    ${params2}=    Create Dictionary    name=Man    actorId=nm654321
    ${params3}=    Create Dictionary    movieId=nm0123456    actorId=nm654321
    ${paramsExpectedT}=    Evaluate    {"movieId": "nm0123456", "actorId": "nm654321", "hasRelationship": ${True}}
    ${paramsExpectedF}=    Evaluate    {"movieId": "nm0123456", "actorId": "nm654321", "hasRelationship": ${False}}

    #Add nodes
    ${resp1}=    PUT On Session    localhost    /api/v1/addMovie    json=${params1}    headers=${headers}    expected_status=200
    ${resp2}=    PUT On Session    localhost    /api/v1/addActor    json=${params2}    headers=${headers}    expected_status=200

    ${url}=    Set Variable    /api/v1/hasRelationship?movieId=nm0123456&actorId=nm654321

    #Test before adding relationship
    ${resp4}=    GET On Session    localhost    url=${url}    headers=${headers}    expected_status=200
    ${resp4_dict}=    Evaluate    json.loads('''${resp4.text}''')
    Should Be Equal    ${paramsExpectedF}    ${resp4_dict}

    #Add the relationship
    ${resp3}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params3}    headers=${headers}    expected_status=200

    #Execute GET
    ${resp5}=    GET On Session    localhost    url=${url}    headers=${headers}    expected_status=200

    #Evaluate if result is correct
    ${resp5_dict}=    Evaluate    json.loads('''${resp5.text}''')
    Should Be Equal    ${paramsExpectedT}    ${resp5_dict}

hasRelationshipFail    #Bad request
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${url}=    Set Variable    /api/v1/hasRelationship?moaeertjdkjhuiiuiuhId=nAm111111111111111111111111891
    ${resp1}=    GET On Session    localhost    url=${url}    headers=${headers}    expected_status=400


### Bacon pathing Tests ###

buildGraph
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params1}=    Create Dictionary    name=Keven Bacon    actorId=nm0000102    #Create Keven Bacon node
    ${params2}=    Create Dictionary    actorId=nm1111891    movieId=nm48123    #Connect Bill and Oppenheimer
    ${params3}=    Create Dictionary    actorId=nm1111891    movieId=nm0123456    #Connect Bill and Men (the movie)
    ${params4}=    Create Dictionary    actorId=nm0000102    movieId=nm0123456    #Connect Keven and Men (the movie part2)
    ${params5}=    Create Dictionary    name=Kilometer Morales    actorId=nm621371    #Add an actor for later disjoint tests
    ${params6}=    Create Dictionary    actorId=nm621371    movieId=nm01345    #Connect km Morales and Spider-Man
    ${resp1}=    PUT On Session    localhost    /api/v1/addActor    json=${params1}    headers=${headers}    expected_status=200
    ${resp2}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params2}    headers=${headers}    expected_status=200
    ${resp3}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params3}    headers=${headers}    expected_status=200
    ${resp4}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params4}    headers=${headers}    expected_status=200
    ${resp5}=    PUT On Session    localhost    /api/v1/addActor    json=${params5}    headers=${headers}    expected_status=200
    ${resp6}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params6}    headers=${headers}    expected_status=200


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

computeBaconNumberFail    #No path to bacon
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${result0}=    GET On Session    localhost    url=/api/v1/computeBaconNumber?actorId=nm621371    headers=${headers}    expected_status=404

computeBaconPathPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${paramsExpected}=    Evaluate    {"baconPath": ["nm7001850","nm48123","nm1111891","nm0123456","nm0000102"]}
    ${result}=    GET On Session    localhost    url=/api/v1/computeBaconPath?actorId=nm7001850    headers=${headers}    expected_status=200

    ${result_dict}=    Evaluate    json.loads('''${result.text}''')
    Should Be Equal    ${paramsExpected}    ${result_dict}

computeBaconPathFail    #No path to bacon
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${result0}=    GET On Session    localhost    url=/api/v1/computeBaconPath?actorId=nm621371    headers=${headers}    expected_status=404
