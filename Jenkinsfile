pipeline {
    agent any

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Package') {
            steps {
                sh 'mvn clean package'
            }
        }
    }

    post {
        success {
            echo 'CI pipeline SUCCESS'
        }
        failure {
            echo 'CI pipeline FAILED'
        }
    }
}
