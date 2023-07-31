pipeline {
    agent any

    stages {
        stage('Trigger Airflow DAG') {
            steps {
                script {
                    // Airflow API endpoint URL
                    def airflowApiUrl = 'http://localhost:3000/api/experimental/dags/wine-prediction/dag_runs'

                    // JSON payload to trigger the DAG run
                    def payload = """
                        {
                            "conf": {},
                            "execution_date": "2023-07-28T00:00:00+00:00",
                            "replace_microseconds": false,
                            "run_id": "manual__${BUILD_NUMBER}"
                        }
                    """

                    def airflowApiToken = 'your_api_token'

                    // Add the token to the HTTP request headers
                    def headers = [Authorization: "Bearer ${airflowApiToken}"]

                    // Jenkins environment variable BUILD_NUMBER for unique run_id
                    def response = httpRequest(
                        contentType: 'APPLICATION_JSON',
                        httpMode: 'POST',
                        requestBody: payload,
                        url: airflowApiUrl,
                        customHeaders: headers // Include the token in the request headers
                    )

                    if (response.status == 200) {
                        echo "Airflow DAG triggered successfully."
                    } else {
                        error "Failed to trigger Airflow DAG: ${response.status} - ${response.content}"
                    }
                }
            }
        }
    }
}
