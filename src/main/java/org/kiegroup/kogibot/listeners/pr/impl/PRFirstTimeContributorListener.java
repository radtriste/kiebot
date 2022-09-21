package org.kiegroup.kogibot.listeners.pr.impl;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.jboss.logging.Logger;
import org.kiegroup.kogibot.config.KogibotConfiguration;
import org.kiegroup.kogibot.listeners.pr.PRCommentConfigListener;
import org.kiegroup.kogibot.util.Constants;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepository.Contributor;
import org.kohsuke.github.GitHub;

import io.quarkiverse.githubapp.ConfigFile;
import io.quarkiverse.githubapp.event.PullRequest;

public class PRFirstTimeContributorListener implements PRCommentConfigListener {
    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public void onOpenedPullRequest(
            @PullRequest.Opened @PullRequest.Reopened @PullRequest.Synchronize GHEventPayload.PullRequest prPayLoad,
            @ConfigFile(Constants.KOGIBOT_CONFIG_FILE) Optional<KogibotConfiguration> kogibotConfiguration,
            GitHub github) throws IOException {
        applyForConfig(prPayLoad, kogibotConfiguration, github);
    }

    @Override
    public String getCommentKey() {
        return Constants.KOGITO_CONFIG_KEY_FIRST_TIME_CONTRIBUTOR;
    }

    @Override
    public String getListenerName() {
        return "First-time Contributor";
    }

    @Override
    public void applyWithComment(String comment, GHEventPayload.PullRequest prPayLoad,
            KogibotConfiguration kogibotConfiguration,
            GitHub github) throws IOException {
        String author = prPayLoad.getPullRequest().getUser().getLogin();
        if (isFirstTimeContributor(prPayLoad.getRepository(), author)) {
            prPayLoad.getPullRequest()
                    .comment(String.format("Hello @%s\n%s", author, comment));
        }

    }

    private boolean isFirstTimeContributor(GHRepository ghRepository, String login) throws IOException {
        return !ghRepository.listContributors().toSet().stream().map(Contributor::getLogin).anyMatch(login::equals);
    }
}
