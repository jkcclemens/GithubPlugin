package me.jkcclemens.royalbot.plugins.github.commands;

import me.jkcclemens.royalbot.plugins.github.GithubPlugin;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.commands.CallInfo;
import org.royaldev.royalbot.commands.IRCCommand;

public class GithubCommand implements IRCCommand {

    private final GithubPlugin plugin;

    public GithubCommand(GithubPlugin instance) {
        plugin = instance;
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo callInfo, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                event.respond("Reloaded config.");
            } else if (args[0].equalsIgnoreCase("authorize")) {
                event.respond("In order to increase the amount of Github requests per hour to 5,000, you can authorize with Github.");
                event.respond("If you'd like to do so, please click this link: https://github.com/login/oauth/authorize?client_id=7fd2692efb030c86e449");
                event.respond("Once you get the token, please run " + callInfo.getLabel() + " settoken <token>");
            } else if (args[0].equalsIgnoreCase("removetoken")) {
                if (!plugin.getConfig().isSet("auth.token")) {
                    event.respond("No token set.");
                    return;
                }
                plugin.getConfig().set("auth.token", null);
                plugin.saveConfig();
                event.respond("Token removed.");
            } else if (args[0].equalsIgnoreCase("settoken")) {
                if (args.length < 2) {
                    event.respond("Please specify an auth token.");
                    return;
                }
                plugin.getConfig().set("auth.token", args[1]);
                plugin.saveConfig();
                event.respond("Token set.");
            } else if (args[0].equalsIgnoreCase("help")) {
                event.respond("Subcommands: reload, authorize, settoken, removetoken");
            }
        } else event.respond("Version: " + plugin.getPluginDescription().getVersion());
    }

    @Override
    public String getName() {
        return "github";
    }

    @Override
    public String getUsage() {
        return "<command> (reload|authorize)";
    }

    @Override
    public String getDescription() {
        return "Reports GithubPlugin version";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"gh"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.PRIVATE;
    }

    @Override
    public AuthLevel getAuthLevel() {
        return AuthLevel.ADMIN;
    }
}
