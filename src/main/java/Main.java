import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        List<URI> configs = new LinkedList<>();
        configs.add(Main.class.getClassLoader().getResource("default.xml").toURI());
        Path overrideConfig = Paths.get("override.xml");
        Files.write(overrideConfig, ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<Configuration monitorInterval=\"1\"/>")
                .getBytes(StandardCharsets.UTF_8));
        configs.add(overrideConfig.toUri());
        Configurator.initialize("dispatch", Main.class.getClassLoader(), configs, null);
        Logger logger = LogManager.getLogger(Main.class);

        System.out.println("[test] Expect line: 'Initial log line'");
        logger.info("Initial log line");
        Files.write(overrideConfig, ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Configuration monitorInterval=\"1\">\n" +
                "    <Appenders>\n" +
                "        <Console name=\"STDOUT\" target=\"SYSTEM_OUT\">\n" +
                "            <PatternLayout pattern=\"UPDATED: %m%n\"/>\n" +
                "        </Console>\n" +
                "    </Appenders>\n" +
                "</Configuration>")
                .getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.SYNC);
        Thread.sleep(3000);
        System.out.println("[test] Expect line: 'UPDATED: Logging after reconfiguration'");
        logger.info("Logging after reconfiguration");
    }
}
