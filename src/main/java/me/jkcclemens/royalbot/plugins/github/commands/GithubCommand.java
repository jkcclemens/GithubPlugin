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
        event.respond("Version: " + plugin.getPluginDescription().getVersion());
    }

    @Override
    public String getName() {
        return "github";
    }

    @Override
    public String getUsage() {
        return "<command>";
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
