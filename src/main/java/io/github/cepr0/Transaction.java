package io.github.cepr0;

import lombok.Value;

@Value
public class Transaction {
	private ContextFactory contextFactory;
	private Procedure actions;
	private Procedure rollbacks;
	private ExceptionFactory commitException;
	private ExceptionFactory rollbackException;

	private Transaction(
			ContextFactory contextFactory,
			Procedure actions,
			Procedure rollbacks,
			ExceptionFactory commitException,
			ExceptionFactory rollbackException
	) {
		this.contextFactory = contextFactory;
		this.actions = actions;
		this.rollbacks = rollbacks;
		this.commitException = commitException;
		this.rollbackException = rollbackException;
	}

	private void execute() {
		if (contextFactory == null) return;
		Object context = this.contextFactory.get();

		try {
			if (actions != null) {
				// log.debug: Tx start
				actions.execute(context);
				// log.debug: Tx end
			}
		} catch (Exception e) {
			// log.debug: Tx failed
			if (rollbacks != null) {
				try {
					// log.debug: Rollback start
					rollbacks.execute(context);
					// log.debug: Rollback end
				} catch (Exception ee) {
					// log.debug: Rollback failed
					if (rollbackException != null) {
						throw rollbackException.thrown(ee);
					} else {
						throw new RuntimeException(ee);
					}
				}
			}
			if (commitException != null) {
				throw commitException.thrown(e);
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	public static TransactionBuilder with(ContextFactory context) {
		TransactionBuilder builder = new TransactionBuilder();
		builder.context = context;
		return builder;
	}

	public static class TransactionBuilder {
		private ContextFactory context;
		private Procedure actions;
		private Procedure rollbacks;
		private ExceptionFactory commitException;
		private ExceptionFactory rollbackException;

		TransactionBuilder() {
		}

		public TransactionBuilder execute(Procedure actions) {
			this.actions = actions;
			return this;
		}

		public TransactionBuilder onErrorThrow(ExceptionFactory commitException) {
			this.commitException = commitException;
			return this;
		}

		public TransactionBuilder onErrorRollback(Procedure rollbacks) {
			this.rollbacks = rollbacks;
			return this;
		}

		public TransactionBuilder onRollbackErrorThrow(ExceptionFactory rollbackException) {
			this.rollbackException = rollbackException;
			return this;
		}

		public void start() {
			new Transaction(context, actions, rollbacks, commitException, rollbackException)
					.execute();
		}

	}
}


