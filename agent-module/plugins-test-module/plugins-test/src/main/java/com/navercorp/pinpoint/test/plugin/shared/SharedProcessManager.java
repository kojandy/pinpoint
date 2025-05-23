/*
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.test.plugin.shared;

import com.navercorp.pinpoint.bootstrap.config.Profiles;
import com.navercorp.pinpoint.test.plugin.ConfigResolver;
import com.navercorp.pinpoint.test.plugin.PinpointPluginTestInstance;
import com.navercorp.pinpoint.test.plugin.PluginForkedTestContext;
import com.navercorp.pinpoint.test.plugin.PluginTestConstants;
import com.navercorp.pinpoint.test.plugin.ProcessManager;
import com.navercorp.pinpoint.test.plugin.ProcessTerminator;
import com.navercorp.pinpoint.test.plugin.junit5.launcher.SharedPluginForkedTestLauncher;
import com.navercorp.pinpoint.test.plugin.util.ClassPath;
import com.navercorp.pinpoint.test.plugin.util.CollectionUtils;
import com.navercorp.pinpoint.test.plugin.util.CommandLineOption;
import com.navercorp.pinpoint.test.plugin.util.StringUtils;
import com.navercorp.pinpoint.test.plugin.util.TestLogger;
import org.eclipse.aether.artifact.Artifact;
import org.tinylog.TaggedLogger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;


/**
 * @author Taejin Koo
 */
public class SharedProcessManager implements ProcessManager {

    private final TaggedLogger logger = TestLogger.getLogger();

    private final PluginForkedTestContext context;
    private final Map<String, List<Artifact>> testRepository = new LinkedHashMap<>();
    private final String sharedClassName;
    private final List<Path> sharedLibs;

    private Process process = null;

    public SharedProcessManager(PluginForkedTestContext context, String sharedClassName, List<Path> sharedLibs) {
        this.context = Objects.requireNonNull(context, "context");
        this.sharedClassName = sharedClassName;
        this.sharedLibs = sharedLibs;
    }

    @Override
    public Process create(PinpointPluginTestInstance pluginTestInstance) {
        return get();
    }

    @Override
    public Process get() {
        if (process == null) {
            try {
                this.process = fork();
            } catch (IOException e) {
                logger.error(e, "process fork failed");
            }
//            this.process = process;
        }
        return process;
    }

    @Override
    public void stop() {
        if (testRepository.isEmpty()) {
            if (process != null) {
                ProcessTerminator terminator = new ProcessTerminator(logger, process);
                terminator.destroy(10, TimeUnit.SECONDS);
                process = null;
            }
        }
    }

    public Process fork() throws IOException {
        List<String> commands = buildCommand();

        ProcessBuilder builder = new ProcessBuilder();

        builder.command(commands);
        builder.redirectErrorStream(true);
        builder.directory(ConfigResolver.workDir());

        logger.info("Working directory: {}", System.getProperty("user.dir"));
        logger.info("Command: {}", builder.command());
        logger.info("CommandSize: {}", builder.command().toString().length());

        this.process = builder.start();
        return process;
    }

    public boolean registerTest(String testId, List<Artifact> artifactList) {
        if (testRepository.containsKey(testId)) {
            return false;
        }

        testRepository.put(testId, artifactList);
        return true;
    }

    public boolean deregisterTest(String testId) {
        List<Artifact> value = testRepository.remove(testId);
        return value != null;
    }

    public Process getProcess() {
        return process;
    }

    public boolean isStarted() {
        return process != null;
    }

    private List<String> buildCommand() {
        CommandLineOption option = new CommandLineOption();

        option.addOption(context.getJavaExecutable());

        option.addOption("-Xmx1024m");
        final List<String> jvmArguments = context.getJvmArguments();

        option.addOptions(jvmArguments);


        option.addOption("-cp");
        String classPath = ClassPath.join(context.getRequiredLibraries());
        option.addOption(classPath);

        option.addOption(getAgent());
        option.addSystemProperty("pinpoint.agentId", "build.test.0");
        option.addSystemProperty("pinpoint.applicationName", "test");
        option.addSystemProperty("java.net.preferIPv4Addresses", "true");

        final String mavenDependencyResolverClassPaths = ClassPath.join(context.getMavenDependencyLibraries());
        option.addSystemProperty(SharedPluginTestConstants.MAVEN_DEPENDENCY_RESOLVER_CLASS_PATHS, mavenDependencyResolverClassPaths);
        if (sharedLibs != null) {
            final String sharedDependencyResolverClassPaths = ClassPath.joinPath(sharedLibs);
            option.addSystemProperty(SharedPluginTestConstants.SHARED_DEPENDENCY_RESOLVER_CLASS_PATHS, sharedDependencyResolverClassPaths);
        }
        if (sharedClassName != null) {
            option.addSystemProperty(SharedPluginTestConstants.SHARED_CLASS_NAME, sharedClassName);
        }
        final String repositoryUrlString = ClassPath.join(context.getRepositoryUrls());
        option.addSystemProperty(SharedPluginTestConstants.TEST_REPOSITORY_URLS, repositoryUrlString);
        option.addSystemProperty(SharedPluginTestConstants.TEST_LOCATION, context.getTestClassLocation());
        option.addSystemProperty(SharedPluginTestConstants.TEST_CLAZZ_NAME, context.getTestClass().getName());

//        list.add("-D" + PINPOINT_TEST_ID + "=" + testCase.getTestId());

        if (context.isDebug()) {
            option.addOptions(getDebugOptions());
        }

        if (context.getProfile() != null) {
            option.addSystemProperty("pinpoint.profiler.profiles.active", context.getProfile());
        }

        if (context.getConfigFile() != null) {
            option.addSystemProperty("pinpoint.config", context.getConfigFile().toString());
            option.addSystemProperty("pinpoint.config.load.mode", "simple");
        }

        Path logLocationConfig = context.getLogLocationConfig();
        if (logLocationConfig != null) {
            option.addSystemProperty(Profiles.LOG_CONFIG_LOCATION_KEY, logLocationConfig.toString());
        }

        option.addOptions(getVmArgs());

        String mainClass = getMainClass();

        if (mainClass.endsWith(".jar")) {
            option.addOption("-jar");
        }

        option.addOption(mainClass);

        Set<Map.Entry<String, List<Artifact>>> testEntries = testRepository.entrySet();
        for (Map.Entry<String, List<Artifact>> testEntry : testEntries) {
            option.addOption(addTest(testEntry.getKey(), testEntry.getValue()));
        }

        return option.build();
    }

    private boolean hasMaxPermSize(List<String> jvmArguments) {
        for (String jvmArgument : jvmArguments) {
            if (jvmArgument.startsWith("-XX:MaxPermSize=")) {
                return true;
            }
        }
        return false;
    }

    private static final String DEFAULT_ENCODING = PluginTestConstants.UTF_8_NAME;

    private List<String> getVmArgs() {
        CommandLineOption option = new CommandLineOption();
        option.addSystemProperty("file.encoding", DEFAULT_ENCODING);
        return option.build();
    }

    private List<String> getDebugOptions() {
        return Arrays.asList("-Xdebug", "-agentlib:jdwp=transport=dt_socket,address=1296,server=y,suspend=y");
    }

    private String getAgent() {
        return String.format("-javaagent:%s=%s", context.getAgentJar(), buildAgentArguments());
    }

    private String buildAgentArguments() {
        final Map<String, String> agentArgumentMap = new LinkedHashMap<>();
        agentArgumentMap.put("AGENT_TYPE", "PLUGIN_TEST");

        final List<String> importPluginIds = context.getImportPluginIds();
        if (CollectionUtils.hasLength(importPluginIds)) {
            String enablePluginIds = String.join(ArtifactIdUtils.ARTIFACT_SEPARATOR, importPluginIds);
            agentArgumentMap.put(PluginTestConstants.AGENT_PARAMETER_IMPORT_PLUGIN, enablePluginIds);
        }
        return join(agentArgumentMap);
    }

    private String join(Map<String, String> map) {
        StringJoiner joiner = new StringJoiner(PluginTestConstants.AGENT_PARSER_DELIMITER);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            joiner.add(entry.getKey() + "=" + entry.getValue());
        }
        return joiner.toString();
    }

    private String addTest(String testId, List<Artifact> artifactList) {
        StringBuilder mavenDependencyInfo = new StringBuilder();
        mavenDependencyInfo.append(testId);
        mavenDependencyInfo.append('=');

        for (Artifact artifact : artifactList) {
            String str = ArtifactIdUtils.artifactToString(artifact);
            if (StringUtils.hasText(str)) {
                mavenDependencyInfo.append(str);
                mavenDependencyInfo.append(ArtifactIdUtils.ARTIFACT_SEPARATOR);
            }
        }

        return mavenDependencyInfo.toString();
    }

    public String getMainClass() {
//        return SharedPinpointPluginTest.class.getName();
        return SharedPluginForkedTestLauncher.class.getName();
    }

}
