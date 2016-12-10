# RxUsa

This is a working Android app that features RxJava prominently in its architecture. 

The model for the app is the structure of the US Federal Government. A Constitution must be ratified to 'log in' to the app. The main screen has tabs for the Executive, Legislative, and Judicial branches of Government. The branches of government communicate with each other.

LoginActivity is the best place to start.

### Improvements

Improvements that make the app more pleasant to use, or function more like the actual US Federal Government are welcome. Simulated voting, negotiations, a 'News' branch, etc.

A few technical areas:
 - _Every branch should have it's own service and persistence layer:_ Right now, the branches are spoonfed information from a centralized message service, but that's not how things work in the real world. Branches should store 'Bills' or government artifacts in their own persistence layers and *trade* information. Ideally, the messaging service doesn't store any information.
 - _Add the concept of Time:_ If the legislature sends a bill to the President and the president doesn't act within 10 days, the bill is automatically signed into law. This would also open the door to election and voting simulations.

 Design Areas:
 - Any ideas to make the user-experience fun and interactive?

### FIXME
- Bottom bar icons need color to reflect their selection state
