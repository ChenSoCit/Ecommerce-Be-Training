pipeline {
    agent any

    tools {
        maven 'maven3'
        jdk 'jdk17'
    }

    triggers {
        githubPush()
    }

    stages {

        stage('CI - Develop') {
            when {
                branch 'develop'
            }
            steps {
                sh 'mvn clean test'
            }
        }

        stage('CI - Pull Request to Master') {
            when {
                allOf {
                    branch 'master'
                    expression { env.CHANGE_ID != null }
                }
            }
            steps {
                sh 'mvn clean test'
            }
        }

        stage('CD - Master Deploy') {
            when {
                allOf {
                    branch 'master'
                    expression { env.CHANGE_ID == null }
                }
            }
            steps {
                sh 'mvn clean package -DskipTests'
                echo 'Deploy production here'
            }
        }
    }
} 