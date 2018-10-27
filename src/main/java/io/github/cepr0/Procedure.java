package io.github.cepr0;

@FunctionalInterface
interface Procedure {
	public void execute(Object context) throws Exception;
}
