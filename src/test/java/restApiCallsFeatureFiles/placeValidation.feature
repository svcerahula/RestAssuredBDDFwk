Feature: Place Validations for the API's

  Scenario: Verify Add Place Functionality using AddPlaceAPI
    Given Add Place Payload
    When user calls "AddPlaceAPI" with Post http request
    Then the API call is successful with status code 200
    And "status" is response body should be "OK"
    And "scope" is response body should be "APP"