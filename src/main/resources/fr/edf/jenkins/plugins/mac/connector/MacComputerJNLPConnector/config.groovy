package fr.edf.jenkins.plugins.mac.connector.MacComputerJNLPConnector

import fr.edf.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)

f.entry(title: _(Messages.Cloud_JenkinsUrl()), field: 'jenkinsUrl') {
    f.textbox()
}
