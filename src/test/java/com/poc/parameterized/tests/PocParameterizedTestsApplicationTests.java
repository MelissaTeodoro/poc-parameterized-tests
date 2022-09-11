package com.poc.parameterized.tests;

import static com.poc.parameterized.tests.util.StingUtils.isPalindrome;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PocParameterizedTestsApplicationTests {

	@Nested
	class WithoutParameterizedTest {

		@DisplayName("Verifica se os inputs são palíndromos (Palavras que permanecem iguais quando lidas de trás para frente)")
		@Test
		void shouldCheckPalindromes() {
			List<String> palindromes = Arrays.asList("racecar", "radar", "able was I ere I saw elba","11");

			for (String palindrome : palindromes )
				Assertions.assertTrue(isPalindrome(palindrome));
		}

		@DisplayName("Verifica se os inputs são nulos ou vazios - Ao todo valida 6 argumentos")
		@Test
		void nullEmptyAndBlankStrings() {
			final List<String> texts = Arrays.asList(" ", "   ", "\t", "\n", null, "");

			for (String text: texts)
				Assertions.assertTrue(text == null || text.trim().isEmpty());
		}

		@DisplayName("Valida como não nulo cada input de uma classe enum")
		@Test
		void shouldCheckEnumSourceWithAutoDetection() {
			Arrays.asList(ChronoUnit.values())
					.forEach(Assertions::assertNotNull);
		}
	}

	@Nested
	static
	class WithParameterizedTest {

		@DisplayName("Verifica se os inputs são palíndromos (Palavras que permanecem iguais quando lidas de trás para frente)")
		@ParameterizedTest
		@ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba", "11" })
		void shouldCheckPalindromes(String candidate) {
			Assertions.assertTrue(isPalindrome(candidate));
		}

		@DisplayName("Verifica se os inputs são nulos ou vazios - Ao todo valida 6 argumentos")
		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { " ", "   ", "\t", "\n" })
		void nullEmptyAndBlankStrings(String text) {
			Assertions.assertTrue(text == null || text.trim().isEmpty());
		}

		@DisplayName("Valida como não nulo cada input de uma classe enum")
		@ParameterizedTest
		@EnumSource(ChronoUnit.class)
		void shouldCheckEnumSource(TemporalUnit unit) {
			Assertions.assertNotNull(unit);
		}

		@DisplayName("Valida como não nulo cada input de uma classe enum")
		@ParameterizedTest
		@EnumSource
		void shouldCheckEnumSourceWithAutoDetection(ChronoUnit unit) {
			Assertions.assertNotNull(unit);
		}

		@DisplayName("Valida os argumentos do método range desconsiderando os 10 primeiros")
		@ParameterizedTest
		@MethodSource("range")
		void testWithRangeMethodSource(int argument) {
			Assertions.assertNotEquals(9, argument);
		}

		static IntStream range() {
			return IntStream.range(0, 20).skip(10);
		}

		@DisplayName("Testa múltiplos argumentos de um MethodSource")
		@ParameterizedTest
		@MethodSource("stringIntAndListProvider")
		void testWithMultiArgMethodSource(String str, int num, List<String> list) {
			Assertions.assertEquals(5, str.length());
			Assertions.assertTrue(num >=1 && num <=2);
			Assertions.assertEquals(2, list.size());
		}

		static Stream<Arguments> stringIntAndListProvider() {
			return Stream.of(
					arguments("apple", 1, Arrays.asList("a", "b")),
					arguments("lemon", 2, Arrays.asList("x", "y"))
			);
		}

		@DisplayName("Testa múltiplos argumentos de um CsvSource (Valores separados por vírgula)")
		@ParameterizedTest
		@CsvSource({
				"apple,         1",
				"banana,        2",
				"'lemon, lime', 0xF1",
				"strawberry,    700_000"
		})
		void testWithCsvSource(String fruit, int rank) {
			Assertions.assertNotNull(fruit);
			Assertions.assertNotEquals(0, rank);
		}

		@DisplayName("Testa múltiplos argumentos de um CsvFileSource (Valores separados por vírgula, ignorando o header do arquivo)")
		@ParameterizedTest
		@CsvFileSource(resources = "/two-column.csv", numLinesToSkip = 1)
		void testWithCsvFileSourceFromClasspath(String country, int reference) {
			Assertions.assertNotNull(country);
			Assertions.assertNotEquals(0, reference);
		}

		@DisplayName("Testa múltiplos argumentos de um CsvFileSource (Valores separados por vírgula, ignorando o header do arquivo)")
		@ParameterizedTest
		@CsvFileSource(files = "src/test/resources/two-column.csv", numLinesToSkip = 1)
		void testWithCsvFileSourceFromFile(String country, int reference) {
			Assertions.assertNotNull(country);
			Assertions.assertNotEquals(0, reference);
		}

		@DisplayName("Testa múltiplos argumentos de um CsvFileSource (Valores separados por vírgula, considerando o header do arquivo)")
		@ParameterizedTest(name = "*****[{index}] - {arguments}*****")
		@CsvFileSource(resources = "/two-column.csv", useHeadersInDisplayName = true)
		void testWithCsvFileSourceAndHeaders(String country, int reference) {
			Assertions.assertNotNull(country);
			Assertions.assertNotEquals(0, reference);
		}

		@DisplayName("Testa múltiplos argumentos de uma classe que implementa ArgumentsProvider")
		@ParameterizedTest
		@ArgumentsSource(MyArgumentsProvider.class)
		void testWithArgumentsSource(String argument) {
			Assertions.assertNotNull(argument);
		}

		static class MyArgumentsProvider implements ArgumentsProvider {
			@Override
			public Stream<Arguments> provideArguments(ExtensionContext context) {
				return Stream.of("111", "banana").map(Arguments::of);
			}
		}
	}

}
