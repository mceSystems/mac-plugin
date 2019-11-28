# Mac Plugin
[![Build Status](https://travis-ci.org/jenkinsci/mac-plugin.svg?branch=master)](https://travis-ci.org/jenkinsci/mac-plugin)
[![Coverage Status](https://coveralls.io/repos/github/jenkinsci/mac-plugin/badge.svg?branch=master)](https://coveralls.io/github/jenkinsci/mac-plugin?branch=master)
[![DepShield Badge](https://depshield.sonatype.org/badges/jenkinsci/mac-plugin/depshield.svg)](https://depshield.github.io)

This plugin allows to configure a cloud of Mac in Jenkins configuration.

Like docker and kubernetes plugins does, you can configure your builds to run on it.

## Features

- [x] Allow to configure a Mac as Jenkins slave
- [x] Run multiples builds on a single Mac
- [x] Run builds on a cloud of Mac
- [x] Clean all files created after each build

This plugin has been tested against macOS 10.14 Mojave and macOS 10.15 Catalina , although theoretically it should work with older version as long as it supports sysadminctl command.

## Requirements

**Restart MacOs after configuration change**

### Enable SSH for all users
Go to System Preferences -> Sharing, and enable Remote Login for All users :

<img src="https://zupimages.net/up/19/47/q7yq.png" width="500"/>

### SSH configuration
In /etc/ssh/sshd_config file, uncomment and update values of parameters MaxAuthTries, MaxSessions, ClientAliveInterval and ClientAliveCountMax to your need.

example of configuration for 10 Jenkins and 1 Mac with 10 users allowed :

- MaxAuthTries 10
- MaxSessions 100
- ClientAliveInterval 30
- ClientAliveCountMax 150

For more informations about sshd_config consult the
[Official Documentation](https://man.openbsd.org/sshd_config)

### Configure a Jenkins User
Create an user on the Mac with administrator privileges. It will be your connection user for Mac Plugin Global configuration.

Add sudo NOPASSWD to this user in /etc/sudoers :
[see how to configure sudo without password](https://www.robertshell.com/blog/2016/12/3/use-sudo-command-osx-without-password)

## Plugin configuration
In jenkins global configuration, add a new Mac Cloud :

<img src="https://www.zupimages.net/up/19/47/e599.png" width="200"/>

Configure fields of Mac Cloud :

<img src="https://zupimages.net/up/19/47/d1i6.png" width="750"/>

Select JNLP for the connector and refer your Jenkins URL. This URL must be accessible by outside, localhost is not working.

Add a new Mac Host and fill the properties in the fields :

<img src="https://zupimages.net/up/19/47/vrte.png" width="750"/>

The number of simultaneous builds on the same Mac Host depends of the property "Max users".
More you have Mac Hosts configured, more you can build simultaneous on many machines.
**For best usage I recommend a limit of 3.**

The supported credentials for now is User and Password.
Put an account of your mac with **sudo NOPASSWORD configured**.

After it refers the label of your agent.
Select JNLP for the connector and refer your Jenkins URL. This URL must be accessible by outside, localhost is not working.

In a project configuration, refers the label :

<img src="https://zupimages.net/up/19/47/xyw2.png" width="750"/>

## Logs configuration
You can define a custom LOGGER to log every output of the plugin on the same place.
To do it, go to System logs in the Jenkins configuration :

<img src="https://zupimages.net/up/19/47/m7i5.png" width="400"/>

Configure the Logger of the plugin :
<img src="https://zupimages.net/up/19/47/3mkc.png" width="750"/>

Save your configuration.

## Execution
After configuration, when you run a job with a Mac Cloud label, it will create a jenkins agent on the mac you setted as host and run the build on it.

You can see it on the home page of Jenkins :

<img src="https://zupimages.net/up/19/47/fkmf.png" width="300"/>

## Contact
Any question ? You can ask it on the [Gitter room](https://gitter.im/jenkinsci/mac-plugin) or open an issue.
