# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [0.2.1] - 2021-01-27

### Added

- Added `.gitattributes`to enforce LF ending.

### Changed

- Refined generics in ItemClass.
- `ItemActionService::getItemsPartitionedByClass(...)` now takes an optional list of `ItemClass` objects to partition for.
  No arguments partitions for all `ItemClass` members.

## [0.2.0] - 2021-01-24

### Added

- Generics and inheritance.

## [0.1.0] - 2021-01-23

### Added

- Lambda streams.