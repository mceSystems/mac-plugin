package fr.edf.jenkins.plugins.mac.connector.MacComputerSSHConnector

import fr.edf.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)

f.entry(title: _(Messages.MacComputerConnector_JvmOptions()), field: 'jvmOptions') {
    f.textbox()
}
f.entry(title: _(Messages.MacComputerConnector_JavaPath()), field: 'javaPath') {
    f.textbox()
}
f.entry(title: _(Messages.MacComputerConnector_PrefixStartSlaveCmd()), field: 'prefixStartSlaveCmd') {
    f.textbox()
}
f.entry(title: _(Messages.MacComputerConnector_SuffixStartSlaveCmd()), field: 'suffixStartSlaveCmd') {
    f.textbox()
}