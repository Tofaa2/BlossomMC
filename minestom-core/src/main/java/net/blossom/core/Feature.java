package net.blossom.core;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerProcess;
import net.minestom.server.command.builder.Command;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.blossom.core.Blossom.LOGGER;

public abstract class Feature {

    private static final HashMap<Class<? extends Feature>, Feature> features = new HashMap<>();
    public static final File DATA_FOLDER = Path.of(".").toFile();

    static void loadFeatures() {
        ServiceLoader<Feature> loader = ServiceLoader.load(Feature.class);
        for (Feature feature : loader) {
            LOGGER.info("Discovered feature " + feature.getName());
            features.put(feature.getClass(), feature);
        }
        List<Feature> loadOrder = generateLoadOrder(new ArrayList<>(features.values()));
        for (Feature feature : loadOrder) {
            MinecraftServer.getGlobalEventHandler().addChild(feature.eventNode);
            feature.init();
            MinecraftServer.getSchedulerManager().buildShutdownTask(feature::terminate);
            Feature.features.put(feature.getClass(), feature);
            LOGGER.info("Loaded feature " + feature.getName());
        }

        for (Feature feature : loadOrder) {
            feature.postInit();
        }
        LOGGER.info("Load order: " + loadOrder.stream().map(Feature::getName).collect(Collectors.joining(", ")));

//        HashMap<Class<? extends Feature>, Feature> features = new HashMap<>();
//        for (Feature feature : loader) {
//            Blossom.LOGGER.info(Component.text("Discovered feature " + feature.getName()));
//            features.put(feature.getClass(), feature);
//        }
//        for (var entry : features.entrySet()) {
//            Feature feature = entry.getValue();
//            loadDepdenencies(feature, features);
//            MinecraftServer.getGlobalEventHandler().addChild(feature.eventNode);
//            feature.init();
//            MinecraftServer.getSchedulerManager().buildShutdownTask(feature::terminate);
//            Feature.features.put(entry.getKey(), entry.getValue());
//        }
//        for (var entry : features.entrySet()) {
//            entry.getValue().postInit();
//        }
    }

    private static List<Feature> generateLoadOrder(List<Feature> features) {
        Map<Feature, List<Feature>> dependencyMap = new HashMap<>();
        {
            Map<Class<? extends Feature>, Feature> featureMap = new HashMap<>();

            for (Feature feature : features) {
                featureMap.put(feature.getClass(), feature);
            }

            allFeatures:
            for (Feature feature : features) {
                FeatureDepends depends = feature.getClass().getAnnotation(FeatureDepends.class);
                int length = depends == null ? 0 : depends.value().length;
                List<Feature> dependencies = new ArrayList<>(length);

                if (depends != null) {
                    for (Class<? extends Feature> dependClass : depends.value()) {
                        Feature dependency = featureMap.get(dependClass);
                        if (dependency == null) {
                            if (Feature.features.containsKey(dependClass)) {
                                dependencies.add(Feature.features.get(dependClass));
                                continue;
                            }
                            else {
                                LOGGER.info("Feature " + feature.getName() + " depends on " + dependClass.getSimpleName() + " but it is not loaded, feature will not be loaded");
                                continue allFeatures;
                            }
                        }
                        dependencies.add(dependency);
                    }
                }
                dependencyMap.put(feature, dependencies);
            }
        }

        LinkedList<Feature> sortedList = new LinkedList<>();

        {
            List<Map.Entry<Feature, List<Feature>>> loadableExtensions;
            // While there are entries with no more elements (no more dependencies)
            while (!(
                    loadableExtensions = dependencyMap.entrySet().stream().filter(entry -> isLoaded(entry.getValue())).toList()
            ).isEmpty()
            ) {
                // Get all "loadable" (not actually being loaded!) extensions and put them in the sorted list.
                for (var entry : loadableExtensions) {
                    // Add to sorted list.
                    sortedList.add(entry.getKey());
                    // Remove to make the next iterations a little quicker (hopefully) and to find cyclic dependencies.
                    dependencyMap.remove(entry.getKey());

                    // Remove this dependency from all the lists (if they include it) to make way for next level of extensions.
                    for (var dependencies : dependencyMap.values()) {
                        dependencies.remove(entry.getKey());
                    }
                }
            }
        }
        // Check if there are cyclic extensions.
        if (!dependencyMap.isEmpty()) {
            LOGGER.error("Minestom found {} cyclic extensions.", dependencyMap.size());
            LOGGER.error("Cyclic extensions depend on each other and can therefore not be loaded.");
            for (var entry : dependencyMap.entrySet()) {
                Feature discoveredExtension = entry.getKey();
                LOGGER.error("{} could not be loaded, as it depends on: {}.",
                        discoveredExtension.getName(),
                        entry.getValue().stream().map(Feature::getName).collect(Collectors.joining(", ")));
            }

        }
        return sortedList;
    }


    /**
     * Checks if this list of extensions are loaded
     *
     * @param extensions The list of extensions to check against.
     * @return If all of these extensions are loaded.
     */
    private static boolean isLoaded(@NotNull List<Feature> extensions) {
        return
                extensions.isEmpty() // Don't waste CPU on checking an empty array
                        // Make sure the internal extensions list contains all of these.
                        || extensions.stream().allMatch(ext -> Feature.features.containsKey(ext.getClass()));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Feature> T getFeature(Class<T> clazz) {
        return (T) features.get(clazz);
    }

    private final String name;
    private final ComponentLogger logger;
    private final EventNode<Event> eventNode;
    private boolean loaded = false;

    public Feature() {
        this.name = getClass().getSimpleName();
        this.logger = ComponentLogger.logger(name);
        this.eventNode = EventNode.all(name);
    }

    public void init() {

    }

    public void postInit() {

    }

    public void terminate() {

    }

    public <T extends Event> void addListener(EventListener<T> listener) {
        eventNode.addListener(listener);
    }

    public <T extends Event> void addListener(Class<T> eventClass, Consumer<T> listener) {
        eventNode.addListener(eventClass, listener);
    }


    public void registerCommands(@NotNull BlossomCommand... commands) {
        for (BlossomCommand command : commands) {
            process().command().register(command);
        }
    }

    public @Nullable BlossomCommand getCommand(@NotNull String name) {
        return (BlossomCommand) process().command().getCommand(name);
    }

    public ServerProcess process() {
        return MinecraftServer.process();
    }

    public String getName() {
        return name;
    }

    public ComponentLogger getLogger() {
        return logger;
    }

    public EventNode<Event> getEventNode() {
        return eventNode;
    }

    public void reload() {

    }

    public static Task sync(Runnable runnable) {
        return MinecraftServer.getSchedulerManager().scheduleTask(runnable, TaskSchedule.immediate(), TaskSchedule.stop(), ExecutionType.SYNC);
    }

    public static Task sync(Runnable runnable, Duration delay) {
        return MinecraftServer.getSchedulerManager().buildTask(runnable).delay(delay).executionType(ExecutionType.SYNC).schedule();
    }

    public static Task sync(Runnable runnable, Duration delay, Duration repeat) {
        return MinecraftServer.getSchedulerManager().buildTask(runnable).delay(delay).repeat(repeat).executionType(ExecutionType.SYNC).schedule();
    }

    public static Task async(Runnable runnable) {
        return MinecraftServer.getSchedulerManager().scheduleTask(runnable, TaskSchedule.immediate(), TaskSchedule.stop(), ExecutionType.ASYNC);
    }

    public static Task async(Runnable runnable, Duration delay) {
        return MinecraftServer.getSchedulerManager().buildTask(runnable).delay(delay).executionType(ExecutionType.ASYNC).schedule();
    }

    public static Task async(Runnable runnable, Duration delay, Duration repeat) {
        return MinecraftServer.getSchedulerManager().buildTask(runnable).delay(delay).repeat(repeat).executionType(ExecutionType.ASYNC).schedule();
    }
}
