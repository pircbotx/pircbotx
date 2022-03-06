[![Build Status](https://travis-ci.org/pircbotx/pircbotx.svg?branch=master)](https://travis-ci.org/pircbotx/pircbotx)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/25ed005ec882435fb5f7ce6b05e097c0)](https://www.codacy.com/app/pircbotx/pircbotx?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=pircbotx/pircbotx/&amp;utm_campaign=Badge_Grade) [![Join the chat at https://gitter.im/pircbotx-irc/Lobby](https://badges.gitter.im/pircbotx-irc/Lobby.svg)](https://gitter.im/pircbotx-irc/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**PircBotX** is a powerful event-based Java IRC library for bots and user clients. Version v2.3 (2022)

* Provides [63 IRC events](http://pircbotx.github.io/pircbotx/latest/apidocs/org/pircbotx/hooks/events/package-summary.html) in multi-threaded listener system, including
  * Extensive Channel/User Model with high performance in high concurrency workload
  * Standard and reverse/passive DCC Chat and Filesharing powered by `java.nio`
  * CTCP VERSION, ACTION, PING, TIME, and FINGER
  * User modes op, voice, halfop, superops, and owner
  * Channel modes invite only, quiet, moderated, secret, limit, key 
  * WHO, NAMES, LIST (Channels), BANLIST, and QUIETLIST
  * 001-005 server info, MOTD, WHOIS
  * Core IRC commands JOIN, QUIT, KICK, BAN, INVITE, AWAY, MODE, TOPIC
  * Nickserv integration with self register and other user's registration status
  * Auto join channels, auto WHO and MODE on channel join
* Connection features
  * [IRCv3 CAP negotiation](https://github.com/pircbotx/pircbotx/wiki/Documentation#cap-support) with native support for SASL, TLS, away-notify, and message tags
  * Robust connection handling with server list, attempt all DNS entries, auto retry, auto reconnect
  * TLS with SSLSocket or STARTTLS upgrade
  * Supports IPv6 IRC servers and DCC clients
  * Output message throttling, auto long line split
  * [WEBIRC authentication](https://github.com/pircbotx/pircbotx/wiki/Documentation#webirc-authentication)
  * Built in [Ident server](https://github.com/pircbotx/pircbotx/wiki/Documentation#ident-server)
  * MultiBotManager utility runs multiple bots on multiple servers
* Formatting utility for color, bold/underline/italics, and reverse
* Adds context info to Slf4j MDC, Marker on IO logs
* Java 8 to 17 Compatible
* High performance Parser

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

## Status

PircBotX is stable with a majority of the modern IRC spec implemented. Feature requests and pull requests welcome.
