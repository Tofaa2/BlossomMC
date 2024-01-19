package net.blossom.hub.features;

import net.blossom.core.BlossomCommand;
import net.blossom.hub.Hub;
import net.blossom.hub.features.HubPlayer;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.arguments.number.ArgumentNumber;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShardsCommand extends BlossomCommand {


    public ShardsCommand() {
        super("commands.shards", "shards");
        ArgumentEnum<Operation> operationArg = ArgumentType.Enum("operation", Operation.class);
        ArgumentNumber<Integer> amountArg = ArgumentType.Integer("amount").between(0, Integer.MAX_VALUE);
        ArgumentEntity targetArg = ArgumentType.Entity("target").singleEntity(true).onlyPlayers(true);

        addSyntax((sender, context) -> updateShards((HubPlayer) sender, context.get(targetArg).findFirstPlayer(sender), context.get(operationArg), context.get(amountArg)), operationArg, targetArg, amountArg);
        addSyntax((sender, context) -> updateShards((HubPlayer) sender, (HubPlayer) sender, context.get(operationArg), context.get(amountArg)), operationArg, amountArg);
        addSyntax((sender, context) -> {
            Player target = context.get(targetArg).findFirstPlayer(sender);
            if (target == null) {
                sender.sendMessage("<red>Invalid target.");
                return;
            }
            sender.sendMessage("<green>" + target.getUsername() + "<green> has <white>" + ((HubPlayer) target).getShards() + "<green> shards.");
        }, targetArg);
    }

    private void updateShards(HubPlayer player, Player target, Operation operation, int amount) {
        if (target == null) {
            player.sendMessage("<red>Invalid target.");
            return;
        }
        HubPlayer hubTarget = (HubPlayer) target;
        switch (operation) {
            case ADD:
                hubTarget.addShards(amount);
                break;
            case REMOVE:
                hubTarget.removeShards(amount);
                break;
            case SET:
                hubTarget.setShards(amount);
                break;
            case RESET:
                hubTarget.setShards(0);
                break;
            case MULTIPLY:
                hubTarget.setShards(player.getShards() * amount);
                break;
            case DIVIDE:
                hubTarget.setShards(player.getShards() / amount);
                break;
            case MODULO:
                hubTarget.setShards(player.getShards() % amount);
                break;
        }
        player.sendMessage("<green>Successfully update shards of <white>" + target.getUsername() + "<green> to <white>" + hubTarget.getShards() + "<green> shards.");
    }

    public enum Operation {
        ADD,
        REMOVE,
        SET,
        RESET,
        MULTIPLY,
        DIVIDE,
        MODULO
    }

}
