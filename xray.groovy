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
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: '04bab23d-6247-42cd-a135-155a3e34f5b9', url: 'https://github.com/anshuman0053/postman-newman.git']])
            }
        }
        
        stage('Build Xray token python script'){
            steps{
                git branch: 'main', credentialsId: '04bab23d-6247-42cd-a135-155a3e34f5b9', url: 'https://github.com/anshuman0053/postman-newman.git'
                sh "pip install requests"
                sh 'python3 postman_upload.py'
            }
        }
        
        stage('Running Postman API Collection via Newman'){
            steps{
                sh "pwd"
                sh "newman run /home/anshuman-slathia/newman/POC_google.json -r 'cli,junitfull,junitxray' --reporter-junitfull-export postman_xray_junitfull.xml --reporter-junitxray-export postman_xray_junitxray.xml -n 1"
            }
        }
        stage('Uploading Newman reports to Xray JIRA'){
            steps{
                sh "pwd"
                sh '''curl -H 'Content-Type: text/xml' -X POST -H "Authorization': token" --data @postman_xray_junitfull.xml https://xray.cloud.getxray.app/api/v2/import/execution/junit?projectKey=CQ'''
            }
        }
        
        stage('Test Pipeline'){
            steps{
                echo "Pipeline Tested Successuly"
            }
        }
    }
}
