package me.jkcclemens.royalbot.plugins.github.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.listeners.IRCListener;
import org.royaldev.royalbot.listeners.Listener;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLListener implements IRCListener {

    private final ObjectMapper om = new ObjectMapper();
    private final Pattern p = Pattern.compile("https?://github\\.com/(\\w+)/(\\w+)/pull/(\\d+)", Pattern.CASE_INSENSITIVE);

    @Override
    public String getName() {
        return "github-url-listener";
    }

    @Listener
    public void urlSent(MessageEvent e) {
        final Matcher m = p.matcher(e.getMessage());
        while (m.find()) {
            if (m.groupCount() < 3) continue;
            final JsonNode jn = getPullData(m.group(1), m.group(2), m.group(3));
            if (jn == null) continue;
            e.respond(String.format("Pull request on %s: " + Colors.BOLD + "%s" + Colors.NORMAL + " by " + Colors.BOLD + "%s" + Colors.NORMAL + " (%s)",
                    jn.path("base").path("repo").path("full_name").asText(),
                    jn.path("title").asText(),
                    jn.path("user").path("login").asText(),
                    jn.path("state").asText()
            ));
        }
    }

    private JsonNode getPullData(String user, String repo, String pull) {
        try {
            return om.readTree(BotUtils.getContent(String.format("https://api.github.com/repos/%s/%s/pulls/%s", user, repo, pull)));
        } catch (IOException ex) {
            return null;
        }
    }
}
