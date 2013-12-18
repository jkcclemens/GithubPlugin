package me.jkcclemens.royalbot.plugins.github;

import me.jkcclemens.royalbot.plugins.github.commands.GithubCommand;
import me.jkcclemens.royalbot.plugins.github.listeners.URLListener;
import org.royaldev.royalbot.handlers.CommandHandler;
import org.royaldev.royalbot.handlers.ListenerHandler;
import org.royaldev.royalbot.plugins.IRCPlugin;

public class GithubPlugin extends IRCPlugin {

    @Override
    public void onEnable() {
        CommandHandler ch = getBot().getCommandHandler();
        ch.register(new GithubCommand(this));

        ListenerHandler lh = getBot().getListenerHandler();
        lh.register(new URLListener());

        getLogger().info("Enabled!");
    }
}
