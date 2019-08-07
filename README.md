# Interactive Fuzzy Sorting

### Idea
A Set of items is to be sorted roughly (e.g. Smash characters by their strength).
To do so, they are represented as graph nodes, and comparisons between them (e.g. 1v1 results) are edges.
This graph tries to layout itself nicely according to the forces implied by the edges.
The resulting order of the items is roughly sorted.

## Algo details
One item is the root, all connected items are the trunk.
While there are items not connected to the trunk, they get compared to the middlemost item of the trunk in the current order.
After that, the graph of items is connected.
Now, pairs of logically distant nodes are searched (high amount of edges required to get from one to the other) and are also connected.
This way, with only a few connections more than n, pathes inside the graph get pretty short, and the layout represents the intrinsic order of the items better.