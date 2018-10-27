package io.github.cepr0;

@FunctionalInterface
public interface ContextFactory {
	public Object get();
}
