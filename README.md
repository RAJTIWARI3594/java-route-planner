# Java Route Planner

Find shortest travel-time paths through a local campus graph, optionally avoiding closed locations. Includes deterministic Dijkstra routing, CSV validation and route instructions.

## Quick start

```sh
sh build.sh
java -cp build RoutePlanner sample/campus.csv GATE LIBRARY
java -cp build RoutePlanner sample/campus.csv GATE LAB --avoid CAFETERIA
```
CSV is `from,to,minutes`; routes are undirected. JDK 17+, no external dependencies.

## Tests

`sh test.sh`

## Scope and limitations

Synthetic campus, not GPS/navigation or live traffic. Positive finite edge minutes only. Avoided endpoints are rejected. CSV uses simple unquoted identifier fields; duplicate undirected edges rejected. No external mapping API or coordinate inference.

## Attribution and sample data

Built for Raj's portfolio with Wabi AI assistance. Sample data is invented and labelled synthetic; no real customer, employer or private account records are included. This is a learning/portfolio implementation, not a claim of commercial work or production deployment.

See source and tests for implementation decisions. Use invented data when exploring.

## How it works

CSV → validated undirected adjacency graph → Dijkstra priority queue → predecessor reconstruction. Closed locations are removed from traversal, not merely hidden in output.

## Verified example

Actual JDK17 compilation and routes to LIBRARY/LAB verified. Avoid-CAFETERIA route excluded that location. No GPS or live mapping claims.

See [VALIDATION.md](VALIDATION.md) for tested environments and scope.
