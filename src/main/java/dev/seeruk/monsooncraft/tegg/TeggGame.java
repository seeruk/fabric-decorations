package dev.seeruk.monsooncraft.tegg;

import com.google.gson.JsonObject;
import dev.seeruk.monsooncraft.tegg.event.TeggPlacedCallback;
import dev.seeruk.monsooncraft.tegg.event.TeggRetrievedCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static dev.seeruk.monsooncraft.MonsoonCraftMod.LOGGER;

public class TeggGame {
    public static final Identifier TEGG_DURATION = new Identifier("tegg", "tegg_duration");

    public static final Integer MAXIMUM_TICKS = 12000000; // 10000 "minutes"

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void initialise() {
        Registry.register(Registries.CUSTOM_STAT, "tegg_duration", TEGG_DURATION);
        Stats.CUSTOM.getOrCreateStat(TEGG_DURATION, StatFormatter.TIME);

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            // Set up the scoreboard
            var scoreboard = server.getScoreboard();

            if (!scoreboard.containsObjective("tegg_owner")) {
                scoreboard.addObjective("tegg_owner", ScoreboardCriterion.DUMMY, Text.of("Tegg Owner"), ScoreboardCriterion.RenderType.INTEGER);
            }
            if (!scoreboard.containsObjective("tegg_placer")) {
                scoreboard.addObjective("tegg_placer", ScoreboardCriterion.DUMMY, Text.of("Tegg Placer"), ScoreboardCriterion.RenderType.INTEGER);
            }
            if (!scoreboard.containsObjective("tegg_ticks")) {
                scoreboard.addObjective("tegg_ticks", ScoreboardCriterion.DUMMY, Text.of("Tegg Ticks"), ScoreboardCriterion.RenderType.INTEGER);
            }
            if (!scoreboard.containsObjective("tegg_score")) {
                scoreboard.addObjective("tegg_score", ScoreboardCriterion.DUMMY, Text.of("Tegg Score"), ScoreboardCriterion.RenderType.INTEGER);
            }
            if (!scoreboard.containsObjective("tegg_winner")) {
                scoreboard.addObjective("tegg_winner", ScoreboardCriterion.DUMMY, Text.of("Tegg Winner"), ScoreboardCriterion.RenderType.INTEGER);
            }
        });

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            var scoreboard = server.getScoreboard();

            var winner = scoreboard.getAllPlayerScores(scoreboard.getObjective("tegg_winner")).stream().findFirst();
            if (winner.isPresent()) {
                return;
            }

            var placerScore = scoreboard.getAllPlayerScores(scoreboard.getObjective("tegg_placer")).stream()
                    .filter((score) -> score.getScore() == 1)
                    .findFirst();

            if (placerScore.isPresent()) {
                var playerName = placerScore.get().getPlayerName();

                var ticks = scoreboard.getPlayerScore(playerName, scoreboard.getObjective("tegg_ticks"));
                var score = scoreboard.getPlayerScore(playerName, scoreboard.getObjective("tegg_score"));

                if (ticks != null && score != null) {
                    ticks.incrementScore();
                    score.setScore(ticks.getScore() / 20 / 60); // Convert ticks to minutes

                    if (ticks.getScore() >= MAXIMUM_TICKS) {
                        scoreboard.getPlayerScore(playerName, scoreboard.getObjective("tegg_winner")).setScore(1);
                    }

                    // If the player is online, update their stats.
                    var player = server.getPlayerManager().getPlayer(playerName);
                    if (player != null && ticks.getScore() % 20 == 0) { // Update stats every second
                        player.getStatHandler().setStat(player, Stats.CUSTOM.getOrCreateStat(TEGG_DURATION), ticks.getScore());
                    }
                }
            }
        });

        TeggPlacedCallback.EVENT.register((player) -> {
            var server = player.getServer();
            if (server == null) {
                return ActionResult.PASS;
            }

            var scoreboard = server.getScoreboard();
            var owner = scoreboard.getObjective("tegg_owner");
            var placer = scoreboard.getObjective("tegg_placer");

            // Reset all player owner / placer states
            scoreboard.getAllPlayerScores(owner).forEach((score) -> score.setScore(0));
            scoreboard.getAllPlayerScores(placer).forEach((score) -> score.setScore(0));

            // The placer must be the owner, so fix that if it was broken
            scoreboard.getPlayerScore(player.getName().getString(), owner).setScore(1);
            scoreboard.getPlayerScore(player.getName().getString(), placer).setScore(1);

            // Notify Discord
            sendDiscordNotification(String.format("%s has placed the Tegg", player.getName().getString()));

            return ActionResult.PASS;
        });

        TeggRetrievedCallback.EVENT.register((player) -> {
            var server = player.getServer();
            if (server == null) {
                return ActionResult.PASS;
            }

            var scoreboard = server.getScoreboard();
            var owner = scoreboard.getObjective("tegg_owner");
            var placer = scoreboard.getObjective("tegg_placer");

            // Nobody is the placer now if it's been retrieved, and we "fix" the owner
            scoreboard.getAllPlayerScores(owner).forEach((score) -> score.setScore(0));
            scoreboard.getAllPlayerScores(placer).forEach((score) -> score.setScore(0));

            // But the retriever now owns the Tegg
            scoreboard.getPlayerScore(player.getName().getString(), owner).setScore(1);

            // Notify Discord
            sendDiscordNotification(String.format("%s has retrieved the Tegg", player.getName().getString()));

            return ActionResult.PASS;
        });
    }

    public static boolean isItemStackTegg(ItemStack stack) {
        return Registries.ITEM.getId(stack.getItem()).toString().equals("minecraft:dragon_egg");
    }

    private static void sendDiscordNotification(String message) {
        var webhookUrl = System.getenv("TEGG_DISCORD_WEBHOOK_URL");
        if (webhookUrl == null) {
            return;
        }

        var body = new JsonObject();
        body.addProperty("content", message);

        var req = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .timeout(Duration.ofMillis(10000))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        httpClient.sendAsync(req, HttpResponse.BodyHandlers.discarding()).thenApply(response -> {
            if (response.statusCode() >= 400) {
                LOGGER.warn("failed to send Tegg notification");
            }
            return response;
        });
    }
}
