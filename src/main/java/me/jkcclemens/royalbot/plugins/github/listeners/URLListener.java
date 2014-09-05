package me.jkcclemens.royalbot.plugins.github.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jkcclemens.royalbot.plugins.github.GithubPlugin;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;
import org.royaldev.royalbot.BotUtils;
import org.royaldev.royalbot.listeners.IRCListener;
import org.royaldev.royalbot.listeners.Listener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLListener implements IRCListener {

    private final GithubPlugin plugin;
    private final ObjectMapper om = new ObjectMapper();
    private final Pattern pullRequest = Pattern.compile("https?://github\\.com/(\\w+)/(\\w+)/pull/(\\d+)/?");
    private final Pattern repository = Pattern.compile("https?://github\\.com/(\\w+)/(\\w+)/?");
    private final Pattern commit = Pattern.compile("https?://github\\.com/(\\w+)/(\\w+)/commit/(\\w{40})/?");
    private final Pattern user = Pattern.compile("https?://github\\.com/(\\w+)/?");

    public URLListener(GithubPlugin instance) {
        plugin = instance;
    }

    @Override
    public String getName() {
        return "github-url-listener";
    }

    @Listener
    public void pullRequest(MessageEvent e) {
        for (String[] matches : getMatches(pullRequest, e.getMessage())) {
            if (matches.length < 3) continue;
            matches = urlEncodeStrings(matches);
            final JsonNode jn = getAPIData("https://api.github.com/repos/%s/%s/pulls/%s", matches[0], matches[1], matches[2]);
            if (jn == null) continue;
            e.respond(String.format("Pull request on %s: " + Colors.BOLD + "%s" + Colors.NORMAL + " by " + Colors.BOLD + "%s" + Colors.NORMAL + " (%s)",
                    jn.path("base").path("repo").path("full_name").asText(),
                    jn.path("title").asText(),
                    jn.path("user").path("login").asText(),
                    jn.path("state").asText()
            ));
        }
    }

    @Listener
    public void repository(MessageEvent e) {
        for (String[] matches : getMatches(repository, e.getMessage())) {
            if (matches.length < 2) continue;
            matches = urlEncodeStrings(matches);
            final JsonNode jn = getAPIData("https://api.github.com/repos/%s/%s", matches[0], matches[1]);
            if (jn == null) continue;
            e.respond(String.format("Repository %s (%s): \"%s\" %s stars, %s watchers, %s forks.",
                    jn.path("full_name").asText(),
                    jn.path("language").asText(),
                    jn.path("description").asText(),
                    jn.path("stargazers_count").asInt(),
                    jn.path("subscribers_count").asInt(),
                    jn.path("forks_count").asInt()
            ));
        }
    }

    @Listener
    public void commit(MessageEvent e) {
        for (String[] matches : getMatches(commit, e.getMessage())) {
            if (matches.length < 3) continue;
            matches = urlEncodeStrings(matches);
            final JsonNode jn = getAPIData("https://api.github.com/repos/%s/%s/commits/%s", matches[0], matches[1], matches[2]);
            if (jn == null) continue;
            e.respond(String.format("Commit on %s: \"%s\" by %s (%s) - %s file actions: %s additions, %s deletions.",
                    matches[0] + "/" + matches[1],
                    jn.path("commit").path("message").asText().split("\r?\n")[0],
                    jn.path("commit").path("committer").path("name").asText(),
                    jn.path("committer").path("login").asText(),
                    jn.path("stats").path("total").asInt(),
                    jn.path("stats").path("additions").asInt(),
                    jn.path("stats").path("deletions").asInt()
            ));
        }
    }

    @Listener
    public void user(MessageEvent e) {
        for (String[] matches : getMatches(user, e.getMessage())) {
            if (matches.length < 1) continue;
            final JsonNode jn = getAPIData("https://api.github.com/users/%s", matches[0]);
            if (jn == null) continue;
            e.respond(String.format("%s %s (%s) from %s. %s. %s public repos. %s. Following %s. %s followers.",
                    jn.path("type").asText(),
                    (jn.path("name").isNull()) ? jn.path("login").asText() : jn.path("name").asText(),
                    jn.path("login").asText(),
                    (jn.path("location").isNull()) ? "nowhere" : jn.path("location").asText(),
                    (jn.path("company").isNull()) ? "No company" : "Works at " + jn.path("company").asText(),
                    jn.path("public_repos").asInt(),
                    (jn.path("hireable").asBoolean()) ? "Up for hire" : "Not up for hire",
                    jn.path("following").asInt(),
                    jn.path("followers").asInt()
            ));
        }
    }

    private List<String[]> getMatches(Pattern pattern, String message) {
        final List<String[]> groups = new ArrayList<>();
        for (String word : message.split(" ")) {
            final Matcher matcher = pattern.matcher(word);
            if (!matcher.matches()) continue;
            String[] baseGroups = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) baseGroups[i] = matcher.group(i + 1);
            groups.add(baseGroups);
        }
        return groups;
    }

    private JsonNode getAPIData(String url, Object... objects) {
        if (plugin.getConfig().isSet("auth.token"))
            url += "?access_token=" + plugin.getConfig().getString("auth.token");
        try {
            return om.readTree(BotUtils.getContent(String.format(url, objects)));
        } catch (IOException ex) {
            return null;
        }
    }

    private String[] urlEncodeStrings(String[] toEncode) {
        for (int i = 0; i < toEncode.length; i++) {
            try {
                toEncode[i] = URLEncoder.encode(toEncode[i], "UTF-8");
            } catch (UnsupportedEncodingException ignored) {}
        }
        return toEncode;
    }
}
