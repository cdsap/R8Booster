package io.github.cdsap.r8booster

import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinDaemonKillCommandTest {

    @Test
    fun `default command line preserves daemon selection and kill behavior`() {
        assertEquals(
            listOf(
                "sh",
                "-c",
                "jps | grep -E \"KotlinCompileDaemon\" | awk '{print \$1}' | xargs -r kill -9",
            ),
            KotlinDaemonKillCommand.commandLine,
        )
    }
}
