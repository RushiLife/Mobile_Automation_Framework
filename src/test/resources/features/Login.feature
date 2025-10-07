Feature: To verify Login functionality

  @smoke
  Scenario Outline: Test Valid Data for Login functionality

    Given Launch Mobile App
    Then Enter Username "<username>"
    Then Enter Password "<password>"
    And Click on login button

    Examples:
      | username     | password        |
      | test         |   test123       |