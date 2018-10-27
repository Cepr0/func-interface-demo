package io.github.cepr0;

public class App {
	public static void main(String[] args) {

		Transaction.with(() -> "context1")
				.execute(App::actions)
				.onErrorRollback(App::rollbacks)
				.onErrorThrow(RuntimeException::new)
				.onRollbackErrorThrow(RuntimeException::new)
				.start();
	}

	private static void actions(Object context) throws Exception {
		System.out.println("Execute with " + context);
		throw new Exception("Ups!");
	}

	private static void rollbacks(Object context) throws Exception {
		System.out.println("Rollback with " + context);
		throw new Exception("Ups when rollback!");
	}
}
