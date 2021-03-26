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
            sh 'sudo apt -y install maven wget'
            sh '''#sudo apt -y install default-jdk
#sudo apt -y install default-jdr'''
            sh 'wget https://launchpad.net/~linuxuprising/+archive/ubuntu/java/+files/oracle-java11-installer-local_11.0.10-1~linuxuprising0_amd64.deb'
            sh 'apt -y install ./oracle-java11-installer-local_11.0.10-1~linuxuprising0_amd64.deb'
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