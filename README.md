# Demo repo for kaocha#330

See [kaocha#330](https://github.com/lambdaisland/kaocha/issues/330) for context.

This repo demonstrates how to use `lambdaisland.classpath/priority-classloader`
to add paths to the classpath at runtime, and to have them take precedence over
other paths.

See [repl_sessions/classpath_poke.clj](repl_sessions/classpath_poke.clj) and [repl_sessions/classpath_poke_2.clj](repl_sessions/classpath_poke_2.clj)

```
.
├── deps.edn
├── env
│   ├── dev
│   │   └── my_app
│   │       └── env_config.clj
│   └── test
│       └── my_app
│           └── env_config.clj
└── repl_sessions
    ├── classpath_poke.clj
    └── classpath_poke_2.clj
```
