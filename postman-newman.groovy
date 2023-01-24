pipeline{
    agent {
        label 'cpu1'
    }
    
    stages{
        stage("Cleanup") {
            steps {
                cleanWs()
            }
        }
        
        stage("NPM"){
            steps{
                sh "install npm"
            }
        }
        
        stage("Installing Newman"){
            steps{
                sh "npm install -g newman"
            }
        }
        
        stage("Running Newman reporters"){
            steps{
                sh 'pwd'
                sh "newman run POC_google.json -r 'cli,junitfull,junitxray' --reporter-junitfull-export postman_jenkins_junitfull.xml --reporter-junitxray-export postman_jenkins_junitxray.xml -n 1"
            }
        }
    }
    
}
