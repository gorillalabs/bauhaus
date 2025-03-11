# Goals

I think it is important to understand why Bauhaus was designed and built the way it was.
This document will outline the goals of Bauhaus (in the first section) and how they were achieved
(in the [Design choices](#design-choices) section).

## Versatile (in terms of runtime environments)

Bauhaus is designed to be run in a variety of environments, from local development to production-grade environments based
on OSv unikernels, Containers, in virtualized environments or bare metal. Even more so, it should be easy to switch between
the different types of environments.

This is especially true for the local development environment, which will look different from the production environment.
In my experience, replicating the production environment locally is extremely hard to get right, especially if you rely on
SaaS services.

In our sense, the environment is more than just the OS layer. It includes how you configure your system,
monitor behaviour (via logging or metrics), how you deploy your application (including in what granularity). And, in my
experience, this changes more often than one thinks.

For example, you start a new project with a small team and you might use a monolithic architecture.
As the team grows, you split out diverse functional cores and separate them to (micro-)services.
However, during most of your development, you connect those functional cores via APIs inside a single JVM
(e.g. using Protocols) for REPL-based development and fast testing cycles
(see my [Euroclojure 2017 talk "Tear down this wall – rethink distributed systems"](https://www.youtube.com/playlist?list=PLZdCLR02grLpzt6WENiHe16-vx74VbCw_)).

You provide different APIs (e.g., one stable and secure production-grade API, and one fast-changing API
for local development, where you push forward the functional development before adding the necessary middleware layers
to cope with the diverse requirements of the different clients). For example, for http-based APIs, during development of the
functionality, we only use the `application/json` content type, and once we have the functionality in place, we add other
necessary content types (e.g., `application/edn` for ClojureScript clients).

## Evolvable systems

Bauhaus is designed for your systems to be evolvable. Whether you start a new endeavor or work on a codebase with a rich history,
nothing is set in stone. We believe in Product thinking instead of Project thinking, and our work being the driver of change,
not prohibitors as in 'the project is finished' (see my
[Cloudland 2024 talk "It's our mission to change the world!
"](https://www.linkedin.com/posts/cbetz_its-our-mission-to-change-the-world-activity-7209548267330678784-C22P)).

This has a number of consequences: You must reduce inter-module dependencies in order to be able to reduce the blast radius
of changes. If you need new functionality of a module in one of your apps, you cannot force anyone else in your environment
to upgrade to. You must be able to upgrade a module in isolation, and you must be able to test the upgrade in isolation.

## Fast feedback cycles

One of the most important things in software development is fast feedback cycles. REPL-based development is a great way to
achieve this, if your code is designed to be used in a REPL. However, we found it beneficial to also have fast feedback cycles
for whole systems and configurations, with the ability to switch implementations of a module or a whole system easily.

We often see that different setups are necessary for different development tasks, so each developer must be able to change
the "wiring" of the system and the configuration easily and independently of others. After all, it's software and not actual wires.

## Secure by default

It should be super-easy to keep your configuration clean of secrets (both in production and development). There shouldn't be
a reason to have secrets in your configuration files.

We even believe that you should not use non-TLS secured connections in your development environment, but we do not want to
enforce this on you. But check out [Getting rid of http in development](https://medium.com/@chris_betz/getting-rid-of-http-in-development-86835969995f).

## Minimize boilerplate
We do not like code generation templates, as they tend to get out of sync with the actual code. We prefer to have a set of libraries
unifying general tasks, and being able to use different versions of those libraries in different parts of the system.

## Versioning

Versioning has to be easy and flexible. We _do not use_ [Semantic Versioning](https://semver.org/).
It's not up to us to decide what is a breaking change for you (or your consumers). Instead, these are our beliefs on versioning:

- Version information should be kept in version control systems, e.g. Git, as completely as possible.
- It should be possible to easily identify the exact source code of a version, e.g. by using Git commit SHAs.
- Built artifacts should be immutable and reproducible.
- It should be possible to identify two different builts.
- It should be easily possible to identify the latest built solely by the built artifact filename.

Following these beliefs, we provide you with the [build tooling](../modules/dev-tooling/build).

# Design choices

## Mono Repo with modules

We chose to use a monorepo with multiple modules (instead of distributing Bauhaus across several Git repositories, utilizing
Git modules, or using a monorepo with a single module). This allows us to have a single source of truth for all our code, and
yet be flexible in what modules we depend upon.

You can utilize the approach either by replicating the structure and using Bauhaus a a dependency, or you can fork Bauhaus
to integrate your applications into your own Bauhaus-driven monorepo.

## Usage of Clojure Deps instead of Leiningen

We chose to use Clojure Deps instead of Leiningen for dependency management. This allows us to have a more flexible
dependency network and ease handling of artifacts. As you can see in [clj-monorepo-template](https://github.com/gorillalabs/clj-monorepo-template),
we explored Leiningen, also.

- We can refer to modules from the same working copy of our repository. That way, we can co-develop modules and applications.
- We can refer to specific versions of modules, and we can refer to modules that are not yet released to a "classical" artifact
repository. This is especially useful for fast feedback cycles and evolvable systems. Also, having fewer "moving parts" in
your development infrastructure saves a lot of headaches, e.g. when trying to secure your supply chain.
- We can depend on specific modules only, so it's easy to use only specific parts of Bauhaus, e.g. the [logging setup](../modules/setup/logging).
  In that sense, Bauhaus is more a set of libraries than a framework.

## Usage of Integrant, Integrant REPL, Aero, and Timbre

Simply because we like them. Shoutouts to all the maintainers and contributors of these libraries! Thanks a lot!
