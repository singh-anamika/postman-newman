pipeline {
    agent any

    stages {
        
    
        stage('Checking NODE version and NPM') {
            steps {
                sh "node -v"
                sh "npm -v"
            }
        }
        
        stage('Checkout'){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'cd4433bd-73ee-4f3c-bb3c-c3f8e10144a8', url: 'https://github.com/singh-anamika/postman-xray.git']])
            }
        }
        
        stage('Build Xray token python script'){
            steps{
                git branch: 'main', credentialsId: 'cd4433bd-73ee-4f3c-bb3c-c3f8e10144a8', url: 'https://github.com/singh-anamika/postman-xray.git'
                //sh 'python3 postman_upload.py'
                
                script{
                        def token = sh(script: "python3 postman_upload.py", returnStdout: true).trim()
                        env.BEARER_TOKEN = token
                        
                }
                sh "echo $BEARER_TOKEN" 
            }
        }
        
        stage('Running Postman API Collection via Newman'){
            steps{
                sh "pwd"
                git branch: 'main', credentialsId: 'cd4433bd-73ee-4f3c-bb3c-c3f8e10144a8', url: 'https://github.com/singh-anamika/postman-xray.git'
                sh "newman run POC_google.json -r 'cli,junitfull,junitxray' --reporter-junitfull-export postman_xray_junitfull.xml --reporter-junitxray-export postman_xray_junitxray.xml -n 1"
            }
        }
        stage('Uploading Newman reports to Xray JIRA'){
            steps{
                sh "pwd"
                sh 'curl -H "Content-Type: text/xml" -X POST -H "Authorization: $BEARER_TOKEN" --data @postman_xray_junitfull.xml https://xray.cloud.getxray.app/api/v2/import/execution/junit?projectKey=CQ'
            }
        }
        
        stage('Test Pipeline'){
            steps{
                echo "Pipeline Tested Successuly"
            }
        }
    }
}

