pipeline {
    agent any

    tools {
        maven 'maven3'     // cấu hình trong Jenkins Global Tool Configuration
        jdk 'jdk17'        // hoặc jdk8 / jdk11 tùy project
    }

    stages {

        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('Package JAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            echo 'Build JAR SUCCESS'
        }

        failure {
            echo 'Build FAILED'
        }
    }
}
