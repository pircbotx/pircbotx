[![Build Status](https://travis-ci.org/pircbotx/pircbotx.svg?branch=master)](https://travis-ci.org/pircbotx/pircbotx)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/25ed005ec882435fb5f7ce6b05e097c0)](https://www.codacy.com/app/pircbotx/pircbotx?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=pircbotx/pircbotx/&amp;utm_campaign=Badge_Grade) [![Join the chat at https://gitter.im/pircbotx-irc/Lobby](https://badges.gitter.im/pircbotx-irc/Lobby.svg)](https://gitter.im/pircbotx-irc/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**November 2017** Project moved from /TheLQ/pircbotx to a new github organisation /pircbotx/pircbotx

**January 2016** PircBotX 2.1 is finally released!

**June 2015** We've moved from Google Code! [Issues, wiki, javadocs, and git mirror are on GitHub](https://github.com/pircbotx/PircBotX). [Mercurial mirror is on BitBucket](http://bitbucket.org/TheLQ/pircbotx)

[Current Version: 2.1](https://github.com/pircbotx/pircbotx/wiki/Downloads) - See [Migration Guide to 2.x](https://github.com/pircbotx/pircbotx/wiki/MigrationGuide2) and [ChangeLog](https://github.com/pircbotx/pircbotx/wiki/ChangeLog#21---january-24-2016) for more information

**PircBotX** is a powerful and flexible Java IRC library forked from the popular PircBot framework, bringing many new up-to-date features and bug fixes in an official alternative distribution.

 * Robust, multi-threaded Event-Listener system with [over 50](http://thelq.github.io/pircbotx/latest/apidocs/org/pircbotx/hooks/events/package-summary.html) supported IRC events
 * Powerful Channel/User Model
 * Native SSL support using SSLSocket or STARTTLS
 * Standard and reverse/passive DCC Chat and Filesharing
 * CTCP VERSION, ACTION, PING, TIME, and FINGER support
 * IPv6 IRC servers and DCC clients
 * Op, voice, halfop, superops, and owner user modes
 * [IRCv3 CAP negotiation](https://github.com/pircbotx/pircbotx/wiki/Documentation#cap-support) with native support for SASL, TLS, away-notify, and message tags
 * [WEBIRC](https://github.com/pircbotx/pircbotx/wiki/Documentation#webirc-authentication) support
 * Built in [Ident server](https://github.com/pircbotx/pircbotx/wiki/Documentation#ident-server)

**Checkout the [Wiki](https://github.com/pircbotx/pircbotx/wiki/) for tutorials and documentation**

## PircBotX in 3 Steps
A brief getting started guide

 * [Download PircBotX](https://github.com/pircbotx/pircbotx/wiki/Downloads)
 * Create and execute the following class:
```java
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class MyListener extends ListenerAdapter {
        @Override
        public void onGenericMessage(GenericMessageEvent event) {
                //When someone says ?helloworld respond with "Hello World"
                if (event.getMessage().startsWith("?helloworld"))
                        event.respond("Hello world!");
        }

        public static void main(String[] args) throws Exception {
                //Configure what we want our bot to do
                Configuration configuration = new Configuration.Builder()
                                .setName("PircBotXUser") //Set the nick of the bot. CHANGE IN YOUR CODE
                                .addServer("irc.freenode.net") //Join the freenode network
                                .addAutoJoinChannel("#pircbotx") //Join the official #pircbotx channel
                                .addListener(new MyListener()) //Add our listener that will be called on Events
                                .buildConfiguration();

                //Create our bot with the configuration
                PircBotX bot = new PircBotX(configuration);
                //Connect to the server
                bot.startBot();
        }
}
```
 * Join the #pircbotx channel on irc.freenode.net and send `?helloworld` . Your bot will respond with `Hello world!` Since its a GenericMessageEvent, it will also respond when private messaged. Congratulations, you just wrote your first bot!

PircBotX can do so much more! [Read the docs for more information](http://github.com/pircbotx/pircbotx/wiki/Documentation).

## Support

[Most answers can be found in the docs](http://github.com/pircbotx/pircbotx/wiki/Documentation), javadocs (http://pircbotx.github.io/pircbotx/latest/apidocs/) and [the wiki](http://github.com/pircbotx/pircbotx/wiki/).

If you can't find an answer, ask on IRC at irc.freenode.net/#pircbotx . [We also have a mailing list](http://groups.google.com/group/pircbotx).

## License 
This project is licensed under GNU GPL v3 to be compatible with the PircBot license. 

It is assumed that commercial users can buy the commercial license of PircBot which grants "modification of the Product's source-code and incorporation of the modified source-code into your software"

The PircBot developer has ignored multiple emails asking for a less restrictive license and clarification of the commercial license. Users can show support by respectfully asking him directly at ![pircbot developer's email](http://pircbotx.github.io/pircbotx/pircbot-email.gif). More up to date information is available at in [Issue #63](https://github.com/pircbotx/pircbotx/issues/63).
