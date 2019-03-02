package it.fvaleri.integ;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

public class CommandModeIT {

    @Test
    void greet() throws InvalidExitValueException, IOException, InterruptedException, TimeoutException {
        final ProcessResult result = new ProcessExecutor()
            .command(command("Fede"))
            .readOutput(true)
            .execute();

        Assertions.assertThat(result.getExitValue()).isEqualTo(0);
        Assertions.assertThat(result.outputUTF8()).contains("Hello Fede");
    }

    protected String[] command(String subject) {
        final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
        final String runner = System.getProperty("quarkus.runner") + (isWindows ? ".exe" : "");
        Assertions.assertThat(Paths.get(runner)).exists();
        return new String[] { runner, "-Dgreeted.subject=" + subject };
    }

}
