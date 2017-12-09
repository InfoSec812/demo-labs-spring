# Demo Script

1. Clone this repository from GitHub
   * `git clone https://github.com/InfoSec812/demo-labs-spring.git`
   * `cd demo-labs-spring`
2. Use ansible-galaxy to install the `casl-ansible` role
   * `ansible-galaxy install --roles-path . -r requirements.yml`
3. Log on to an OpenShift cluster which has at LEAST 4 5Gi persistent volumes available
4. Execute the inventory in this repo
   * `ansible-playbook --connection=local -i inventory casl-ansible/playbooks/openshift-cluster-seed.yml`
5. Wait a few minutes for the CI/CD environment to become available
6. In the OpenShift web console, navigate to "Builds -> Pipelines"
7. The first build WILL fail due to an insecure dependency
8. In the OpenShift console, edit the Pipeline config to use the `Step_1` branch
9. Start the pipeline build and monitor the build. It will FAIL again due to the Zed Attack Proxy finding insecurities in the web application.
10. In the OpenShift console, edit the pipeline config to use the `Step_2` branch
11. Start the pipeline build and monitor the build. It will SUCCEED.
12. Show the Jenkins project page and show the ZAP report, Dependency Check Report, and Unit Test report
13. Show the SonarQube project page