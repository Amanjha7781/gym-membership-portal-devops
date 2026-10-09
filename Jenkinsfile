pipeline {
    agent any
    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }
    environment {
        IMAGE_NAME = 'gym-membership-portal:ci'
    }
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Build and Unit Tests') {
            steps { sh 'mvn -B clean verify' }
            post { always { junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: false } }
        }
        stage('Selenium End-to-End') {
            steps {
                sh '''
                    # Jenkins starts shell steps with xtrace enabled; disable it before reading secrets.
                    set +x
                    set -a
                    . /workspace/.env
                    set +a
                    export PORTAL_BASE_URL=http://gym-portal:8080
                    export SELENIUM_REMOTE_URL=http://selenium:4444/wd/hub
                    docker compose --env-file /workspace/.env --profile e2e -f deployment/docker-compose.yml up -d --build mysql gym-portal selenium
                    trap 'docker compose --env-file /workspace/.env --profile e2e -f deployment/docker-compose.yml stop selenium' EXIT
                    mvn -B -DrunSelenium=true -Dtest=MemberPortalSeleniumTest test
                '''
            }
            post { always { junit testResults: 'target/surefire-reports/TEST-edu.college.gymportal.MemberPortalSeleniumTest.xml', allowEmptyResults: false } }
        }
        stage('Docker Build') {
            steps { sh 'docker build -t "$IMAGE_NAME" .' }
        }
        stage('Deploy') {
            when { expression { env.BRANCH_NAME == 'main' || env.GIT_BRANCH == 'origin/main' } }
            steps {
                sh 'docker compose --env-file /workspace/.env -f deployment/docker-compose.yml up -d --build'
            }
        }
    }
    post {
        always { echo "Pipeline finished with status: ${currentBuild.currentResult}" }
    }
}
