pipeline {
  agent {
    node {
      label 'instance1'
    }

  }
  stages {
    stage('Environment') {
      parallel {
        stage('Environment') {
          steps {
            sh 'sudo apt update'
            sh 'sudo apt -y install maven'
            sh '''sudo apt -y install default-jdk
sudo apt -y install default-jdr'''
          }
        }

        stage('error') {
          steps {
            sh '#mvn -B verify --file pom.xml install'
          }
        }

      }
    }

    stage('Test') {
      steps {
        sh '''echo "Versions Java"
sudo update-alternatives --config java
'''
      }
    }

    stage('Package') {
      steps {
        sh 'mvn package'
      }
    }

    stage('Build Maven') {
      steps {
        sh '''cd wise-webapp

mvn -U jetty:run-war'''
        sh 'mvn -o jetty:run-war'
      }
    }

    stage('Connection Test') {
      steps {
        sh '''
curl 0.0.0.0:8080
ss -tulpn
'''
      }
    }

  }
}