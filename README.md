# Clongus

## Introduction

Clongus is a tool for synchronising data sources. It differs from other tools in the following ways:
- It is stateless. It does not store user data, it compares state on every run, and acts accordingly
- All data is processed as key-value pairs, so rules for processing REST queries can equally be used for SQL queries
- It is flexible - it abstracts all the ‘things’ in datasources and provides standard methods of processing them
- It is not ‘opinionated’ - it isn’t geared specifically to user management (though it will do this), it is designed to deal with ‘things’ (things having a pretty board definition)
- ‘things’ can be entities, properties of entities, relationships, anything you can express in terms of key-value pairs

## Use cases:

- User lifecycle management
- group and membership management
- calendar, event and subscription management
- privileges management

## Documentation

- [Concepts]([Concepts](https://github.com/jim-reespotter/clongus/wiki/Concepts))
- [Recipe structure - YAML]([yaml](https://github.com/jim-reespotter/clongus/wiki/yaml))
- recipe fragments:
  - [Connections]([Connections](https://github.com/jim-reespotter/clongus/wiki/Connections))
  - [Queries]([Queries](https://github.com/jim-reespotter/clongus/wiki/Queries))
  - [Matchers]([Matchers](https://github.com/jim-reespotter/clongus/wiki/Matchers))
  - [Value generators]([ValueGenerators](https://github.com/jim-reespotter/clongus/wiki/ValueGenerators))
- [Sample recipes]([SampleRecipes](https://github.com/jim-reespotter/clongus/wiki/SampleRecipes))
- [Running Clongus](https://github.com/jim-reespotter/clongus/wiki/RunningClongus)