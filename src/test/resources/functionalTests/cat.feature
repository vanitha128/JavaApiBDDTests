Feature: End2EndTest for API
Cat Facts swagger url: https://catfact.ninja/

    Scenario Outline: user is able to fetch facts with length and limit
        Given Get facts API with length as "<len>", page as "<page>" and limit as "<limit>"
        Then status code equals <statuscode>
        And Response is valid with length, page and limit as "<len>","<page>","<limit>"

        Examples:
            | len | limit | statuscode |    page    |
            | 40  | 2     |200        | 1           |
            | 40  | 2     |200        | 2           |
            | 40  | 3     |200        | 1           |
            | 40  | 1     |200        | 8           |
