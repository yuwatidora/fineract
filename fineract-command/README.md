# fineract-command module

This module contains the pluggable Command execution framework used across Fineract. It provides a lightweight Command pattern abstraction with routing, middleware, and multiple executor implementations (sync, async, Disruptor), plus optional persistence.

The types live under the org.apache.fineract.command package and are divided into a few subpackages:

- org.apache.fineract.command.core
  - Command<T>: a serializable envelope carrying metadata (id, idempotencyKey, createdAt, tenantId, username) and the payload (T).
  - CommandHandler<REQ, RES>: functional contract to handle a Command carrying a given payload type. It provides a default matches(Command) implementation using generics reflection to select handlers based on the payload class.
  - CommandExecutor: abstraction to execute a Command and return a lazy Supplier<RES>. Different executors implement different dispatching models.
  - CommandMiddleware: hook invoked before routing/handling to add cross‑cutting concerns (validation, auth, logging, metrics, idempotency, persistence, etc.). Multiple middlewares can be registered and will be invoked in order.
  - CommandRouter: resolves a Command to a matching CommandHandler.
  - CommandProperties: Spring Boot configuration properties (fineract.command.*) used to configure the executor and Disruptor settings.

- org.apache.fineract.command.implementation
  - DefaultCommandRouter: default router implementation; finds the first handler whose matches() returns true for the command payload.
  - SynchronousCommandExecutor: invokes middlewares, routes the command, and calls the handler on the caller thread.
  - AsynchronousCommandExecutor: runs the same pipeline on a CompletableFuture (default common pool) and returns a Supplier that joins the future.
  - DisruptorCommandExecutor: high‑throughput executor built on LMAX Disruptor. Events flow through a ring buffer; handling happens on Disruptor consumer threads. (Bean configuration is in starter.)
  - DefaultCommandPipeline and other utilities used internally (if present).

- org.apache.fineract.command.persistence
  - domain/CommandEntity, CommandRepository: optional persistence of command metadata for idempotency/auditing. A CommandJsonMapper and CommandMapper support serialization of payloads.

- org.apache.fineract.command.starter
  - CommandAutoConfiguration: Spring Boot auto‑configuration entry point importing the framework beans.
  - CommandConfiguration: component scanning and Disruptor bean definitions. Creates Disruptor and related WaitStrategy when executor type is disruptor.
  - CommandPersistenceConfiguration: JPA mapping and repository wiring for command persistence.

Lifecycle: how a Command is processed
1. A Command<Payload> is constructed (id, idempotencyKey, metadata, payload) and passed to CommandExecutor.execute(command).
2. The selected CommandExecutor (sync/async/disruptor) iterates over all registered CommandMiddleware beans and calls invoke(command) for each.
3. The CommandRouter chooses a CommandHandler based on the command payload type via handler.matches(command) (generics‑based).
4. The CommandExecutor calls handler.handle(command) and returns the result. For async executors, a Supplier that waits on completion is returned.

Executor selection and configuration
- Properties class: org.apache.fineract.command.core.CommandProperties
  - fineract.command.enabled = true|false (default true)
  - fineract.command.executor = sync | async | disruptor (default sync)
  - fineract.command.ringBufferSize = 1024 (Disruptor)
  - fineract.command.producerType = SINGLE | MULTI (Disruptor)
- Beans are conditionally created with @ConditionalOnProperty:
  - SynchronousCommandExecutor → havingValue = sync
  - AsynchronousCommandExecutor → havingValue = async
  - Disruptor beans and executor → havingValue = disruptor

Key extension points
- Add a new CommandHandler
  - Create a Spring bean implementing CommandHandler<YourPayload, YourResult> and implement handle.
  - The default matches method will select your handler when the command payload is an instance of YourPayload (or subtype).
- Add middleware
  - Create a Spring bean implementing CommandMiddleware for cross‑cutting logic; it will be invoked for every command.
- Customize routing
  - Provide your own CommandRouter bean if you need different matching logic (for example, by command name, annotations, or headers). The default router picks the first matching handler and throws CommandHandlerNotFoundException if none is found.
- Choose execution model
  - Set fineract.command.executor to sync, async, or disruptor depending on your performance and threading needs.

Error handling
- If routing fails, CommandHandlerNotFoundException is thrown.
- Middlewares and handlers may throw domain‑specific exceptions; async/disruptor executors propagate these through the returned Supplier.

Notes
- The Supplier<RES> returned by CommandExecutor.execute allows call sites to decide whether to block immediately (get()), pass the Supplier downstream, or compose it. For async/disruptor executors, it defers the join.
- Ensure that only one CommandHandler matches a given payload type to avoid ambiguity; otherwise, customize the router.
- When enabling the Disruptor executor, tune ringBufferSize, producerType, and the wait strategy bean according to workload characteristics.
