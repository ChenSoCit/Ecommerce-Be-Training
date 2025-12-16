pipeline {
    agent any

    environment {
        APP_PORT = "8080"
        JAR_NAME = "app.jar"
        DEPLOY_DIR = "/home/jenkins/app"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build JAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Prepare Deploy Dir') {
            steps {
                sh '''
                mkdir -p ${DEPLOY_DIR}
                cp target/*.jar ${DEPLOY_DIR}/${JAR_NAME}
                '''
            }
        }

        stage('Stop Old App') {
            steps {
                sh '''
                PID=$(lsof -ti tcp:${APP_PORT}) || true
                if [ -n "$PID" ]; then
                    kill -9 $PID
                fi
                '''
            }
        }

        stage('Deploy New App') {
            steps {
                sh '''
                nohup java -jar ${DEPLOY_DIR}/${JAR_NAME} > ${DEPLOY_DIR}/app.log 2>&1 &
                '''
            }
        }
    }
}
