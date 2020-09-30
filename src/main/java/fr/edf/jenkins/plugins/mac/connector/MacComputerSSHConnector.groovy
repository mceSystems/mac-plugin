package fr.edf.jenkins.plugins.mac.connector

import javax.annotation.CheckForNull

import org.apache.commons.lang.exception.ExceptionUtils
import org.jenkinsci.Symbol
import org.jenkinsci.plugins.plaincredentials.FileCredentials
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl

import fr.edf.jenkins.plugins.mac.MacHost
import fr.edf.jenkins.plugins.mac.MacUser
import fr.edf.jenkins.plugins.mac.ssh.SSHCommand
import fr.edf.jenkins.plugins.mac.util.CredentialsUtils
import hudson.Extension
import hudson.model.Descriptor
import hudson.model.TaskListener
import hudson.plugins.sshslaves.SSHLauncher
import hudson.plugins.sshslaves.verifiers.ManuallyProvidedKeyVerificationStrategy
import hudson.plugins.sshslaves.verifiers.SshHostKeyVerificationStrategy
import hudson.slaves.ComputerLauncher
import hudson.slaves.SlaveComputer

/**
 * Connector to launch Mac agent with SSH
 * @author Mathieu Delrocq
 */
public class MacComputerSSHConnector extends MacComputerConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(MacComputerSSHConnector.class)

    @CheckForNull
    private String jvmOptions
    @CheckForNull
    private String javaPath
    @CheckForNull
    private String prefixStartSlaveCmd
    @CheckForNull
    private String suffixStartSlaveCmd
    @CheckForNull
    protected static final Integer RETRY_WAIT_TIME = 2

    @DataBoundConstructor
    public MacComputerSSHConnector() {
    }

    @DataBoundSetter
    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions
    }

    @DataBoundSetter
    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath
    }

    @DataBoundSetter
    public void setPrefixStartSlaveCmd(String prefixStartSlaveCmd) {
        this.prefixStartSlaveCmd = prefixStartSlaveCmd
    }

    @DataBoundSetter
    public void setSuffixStartSlaveCmd(String suffixStartSlaveCmd) {
        this.suffixStartSlaveCmd = suffixStartSlaveCmd
    }

    @Override
    protected ComputerLauncher createLauncher(MacHost host, MacUser user) throws IOException, InterruptedException {
        SshHostKeyVerificationStrategy sshHostKeyVerificationStrategy = new ManuallyProvidedKeyVerificationStrategy(host.macHostKeyVerifier.getKey())
        return new MacSSHLauncher(host, user, this.jvmOptions, this.javaPath, this.prefixStartSlaveCmd, this.suffixStartSlaveCmd, sshHostKeyVerificationStrategy)
    }


    @Extension @Symbol("ssh")
    public static final class DescriptorImpl extends Descriptor<MacComputerConnector> {
        @Override
        public String getDisplayName() {
            return "Connect with SSH"
        }
    }

    /**
     * Custom flavour of SSHLauncher which allows to inject some custom credentials without this one being stored
     * in a CredentialStore
     */
    private static class MacSSHLauncher extends SSHLauncher {

        private MacUser user
        private MacHost host

        public MacSSHLauncher(MacHost macHost, MacUser user, String jvmOptions, String javaPath, String prefixStartSlaveCmd, String suffixStartSlaveCmd, SshHostKeyVerificationStrategy sshHostKeyVerificationStrategy) {
            super(macHost.host,
            macHost.port,
            user.username,
            jvmOptions,
            javaPath,
            prefixStartSlaveCmd,
            suffixStartSlaveCmd,
            macHost.connectionTimeout,
            macHost.maxTries,
            RETRY_WAIT_TIME,
            sshHostKeyVerificationStrategy)
            this.user = user
        }

        /**
         * Create the user on the Mac and upload Keychain file if necessary before launch SSH Slave connection
         * @params computer
         * @params listener
         */
        @Override
        public void launch(final SlaveComputer computer, final TaskListener listener) throws InterruptedException {
            try {
                SSHCommand.createUserOnMac(host, user)
                if(host.uploadKeychain && host.fileCredentialsId != null) {
                    FileCredentials fileCredentials = CredentialsUtils.findFileCredentials(host.fileCredentialsId, Jenkins.get())
                    SSHCommand.uploadKeychain(host, user, fileCredentials)
                }
                super.launch(computer, listener)
            }catch(Exception e) {
                String message = String.format("Error while connecting computer %s due to error %s ",
                        computer.name, ExceptionUtils.getStackTrace(e))
                listener.error(message)
                throw new InterruptedException(message)
            }
        }

        /**
         * Return an ephemeral credentials for this SSH Mac agent
         * @return StandardUsernameCredentials
         */
        @Override
        public StandardUsernameCredentials getCredentials() {
            return new UsernamePasswordCredentialsImpl(
                    CredentialsScope.SYSTEM,
                    user.username,
                    "Ephemeral credentials for mac user " + user.username,
                    user.username,
                    user.password.getPlainText())
        }
    }
}
