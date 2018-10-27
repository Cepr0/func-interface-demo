package io.github.cepr0;

@FunctionalInterface
public interface ExceptionFactory {
	public RuntimeException thrown(Throwable cause);
}
