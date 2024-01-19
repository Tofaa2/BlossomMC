package net.blossom.entity.commands;

import net.blossom.core.BlossomCommand;
import net.blossom.core.Feature;
import net.blossom.core.Rank;
import net.blossom.entity.EntityFeature;
import net.blossom.entity.mob.BaseMob;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

public class SpawnMobCommand extends BlossomCommand {

    public SpawnMobCommand() {
        super(Rank.ADMIN, "spawnmob", "smob", "spawnm", "mob");
        EntityFeature f = Feature.getFeature(EntityFeature.class);
        ArgumentString mobName = ArgumentType.String("mob");
        mobName.setSuggestionCallback((sender, context, suggestion) -> {
            for (String name : f.getBaseMobNames()) {
                suggestion.addEntry(new SuggestionEntry(name));
            }
        });

        ArgumentInteger amount = ArgumentType.Integer("amount");

        addSyntax(((sender, context) -> {
            if (!(sender instanceof Player player)) return;
            BaseMob mob = f.getBaseMob(context.get(mobName));
            int amt = context.get(amount);
            for (int i = 0; i < amt; i++) {
                mob.create().setInstance(player.getInstance(), player.getPosition());
            }
        }), mobName, amount);

        addSyntax(((sender, context) -> {
            if (!(sender instanceof Player player)) return;
            BaseMob mob = f.getBaseMob(context.get(mobName));
            mob.create().setInstance(player.getInstance(), player.getPosition());
        }), mobName);
    }

}
