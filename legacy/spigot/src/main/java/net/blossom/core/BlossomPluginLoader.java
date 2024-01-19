package net.blossom.core;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class BlossomPluginLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        addRepository(resolver, "central", "https://repo.maven.apache.org/maven2/");
        addRepository(resolver, "jitpack", "https://jitpack.io/");
    }


    private void addDepend(MavenLibraryResolver resolver, String... dependencies) {
        for (String dependency : dependencies)
            resolver.addDependency(new Dependency(new DefaultArtifact(dependency), null));
    }

    private void addRepository(MavenLibraryResolver resolver, String name, String url) {
        resolver.addRepository(new RemoteRepository.Builder(name, "default", url).build());
    }


}
