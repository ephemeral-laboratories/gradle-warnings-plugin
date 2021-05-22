package garden.ephemeral.gradle.warnings.internal

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class WarningsParserTest {

    @Test
    fun `warnings are parsed`() {
        val parser = WarningsParser()
        parser.parse("""
            C:\Path\src\main\java\Blah.java:4: warning: [rawtypes] found raw type: java.util.ArrayList
                private List<String> fish = (List<String>) new ArrayList();
                                                               ^
              missing type arguments for generic class java.util.ArrayList<E>
            C:\Path\src\main\java\Blah.java:4: warning: [unchecked] unchecked cast
                private List<String> fish = (List<String>) new ArrayList();
                                                           ^
              required: java.util.List<java.lang.String>
              found:    java.util.ArrayList
            2 warnings
        """.trimIndent())

        val messages = parser.messages
        assertThat(messages).hasSize(2)
        assertThat(messages[0].file).isEqualTo("C:\\Path\\src\\main\\java\\Blah.java")
        assertThat(messages[0].line).isEqualTo("4")
        assertThat(messages[0].type).isEqualTo("warning");
        assertThat(messages[0].category).isEqualTo("rawtypes");
        assertThat(messages[0].message).isEqualTo("""
            found raw type: java.util.ArrayList
                private List<String> fish = (List<String>) new ArrayList();
                                                               ^
              missing type arguments for generic class java.util.ArrayList<E>
        """.trimIndent());
        assertThat(messages[1].file).isEqualTo("C:\\Path\\src\\main\\java\\Blah.java")
        assertThat(messages[1].line).isEqualTo("4")
        assertThat(messages[1].type).isEqualTo("warning");
        assertThat(messages[1].category).isEqualTo("unchecked");
        assertThat(messages[1].message).isEqualTo("""
            unchecked cast
                private List<String> fish = (List<String>) new ArrayList();
                                                           ^
              required: java.util.List<java.lang.String>
              found:    java.util.ArrayList
        """.trimIndent())
    }
}