pipeline {
    agent any

    tools {
        maven 'MAVEN'
        jdk 'JDK17'
    }

    stages {

        /* =========================
           CHECKOUT SOURCE
        ========================= */
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        /* =========================
           BUILD & TEST (FEATURE)
        ========================= */
        stage('Build & Test - Feature') {
            when {
                branch 'feat/auth'
            }
            steps {
                echo 'Build & Test FEATURE branch'
                sh 'mvn clean test'
            }
        }

        /* =========================
           BUILD & TEST (DEVELOP)
        ========================= */
        stage('Build & Test - Develop') {
            when {
                branch 'develop'
            }
            steps {
                echo 'Build & Test DEVELOP branch'
                sh 'mvn clean test'
            }
        }

        /* =========================
           BUILD & TEST (MASTER)
        ========================= */
        stage('Build & Test - Master') {
            when {
                branch 'master'
            }
            steps {
                echo 'Build & Test MASTER branch'
                sh 'mvn clean test'
            }
        }

        /* =========================
           PACKAGE JAR (MASTER ONLY)
        ========================= */
        stage('Package JAR') {
            when {
                branch 'master'
            }
            steps {
                echo  'Package JAR'
                sh 'mvn clean package -DskipTests'
            }
        }

        /* =========================
           DEPLOY (MASTER ONLY)
        ========================= */
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                echo '🚀 Deploy application'

                // sshagent(['ssh-ubuntu-server']) {
                //     sh '''
                //         scp target/*.jar ubuntu@SERVER_IP:/opt/app/app.jar
                //         ssh ubuntu@SERVER_IP "systemctl restart app"
                //     '''
                // }
            }
        }
    }

    post {
        success {
            echo 'PIPELINE SUCCESS'
        }

        failure {
            echo 'PIPELINE FAILED'
        }
    }
}
